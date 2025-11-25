import React, { useState } from 'react';
import { authAPI } from '../api/client';

function LoginPage({ onLogin, onSwitchToRegister }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!username.trim() || !password.trim()) {
        throw new Error('Пожалуйста, заполните все поля');
      }

      const result = await authAPI.login(username, password);
      
      // Передаём данные пользователя
      const userData = {
        username: result.user?.username || result.username,
        userId: result.user?.userId || result.userId,
        email: result.user?.email,
        roles: result.user?.roles
      };
      
      onLogin(userData);
    } catch (err) {
      // Очищаем auth если логин не удался
      localStorage.removeItem('auth');
      localStorage.removeItem('user');
      setError(err.response?.data?.error || err.response?.data?.message || err.message || 'Ошибка входа. Проверьте логин и пароль.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-header">
        <h1>Вход в систему</h1>
        <p>Войдите для работы с табулированными функциями</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="username">Имя пользователя</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Введите имя пользователя"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Пароль</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Введите пароль"
            disabled={loading}
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Вход...' : 'Войти'}
        </button>
      </form>

      <div className="auth-footer">
        <p>
          Нет аккаунта?{' '}
          <button onClick={onSwitchToRegister} disabled={loading}>
            Зарегистрироваться
          </button>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;



