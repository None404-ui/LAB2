import React, { useState } from 'react';
import { authAPI } from '../api/client';

function RegisterPage({ onRegisterSuccess, onSwitchToLogin }) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      // Валидация
      if (!username.trim() || !email.trim() || !password.trim() || !confirmPassword.trim()) {
        throw new Error('Пожалуйста, заполните все поля');
      }

      if (username.length < 3) {
        throw new Error('Имя пользователя должно содержать минимум 3 символа');
      }

      if (!validateEmail(email)) {
        throw new Error('Введите корректный email адрес');
      }

      if (password.length < 6) {
        throw new Error('Пароль должен содержать минимум 6 символов');
      }

      if (password !== confirmPassword) {
        throw new Error('Пароли не совпадают');
      }

      const response = await authAPI.register(username, email, password);
      
      if (response.data.success) {
        setSuccess('Регистрация успешна! Перенаправление на страницу входа...');
        setTimeout(() => {
          onRegisterSuccess();
        }, 2000);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || 
        err.message || 
        'Ошибка регистрации. Попробуйте снова.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-header">
        <h1>Регистрация</h1>
        <p>Создайте аккаунт для работы с функциями</p>
      </div>

      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="username">Имя пользователя</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Минимум 3 символа"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="example@mail.com"
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
            placeholder="Минимум 6 символов"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="confirmPassword">Подтверждение пароля</label>
          <input
            id="confirmPassword"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Повторите пароль"
            disabled={loading}
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Регистрация...' : 'Зарегистрироваться'}
        </button>
      </form>

      <div className="auth-footer">
        <p>
          Уже есть аккаунт?{' '}
          <button onClick={onSwitchToLogin} disabled={loading}>
            Войти
          </button>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;





