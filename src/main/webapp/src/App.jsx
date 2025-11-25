import React, { useState, useEffect } from 'react';
import './App.css';
import { SettingsProvider } from './context/SettingsContext';
import LoginPage from './components/LoginPage';
import RegisterPage from './components/RegisterPage';
import Dashboard from './components/Dashboard';

function App() {
  const [currentPage, setCurrentPage] = useState('login');
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Проверяем, есть ли сохраненный пользователь
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
      setCurrentPage('dashboard');
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
    setCurrentPage('dashboard');
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('auth');
    setCurrentPage('login');
  };

  const handleRegisterSuccess = () => {
    setCurrentPage('login');
  };

  return (
    <SettingsProvider>
      <div className="app">
        {currentPage === 'login' && (
          <LoginPage
            onLogin={handleLogin}
            onSwitchToRegister={() => setCurrentPage('register')}
          />
        )}
        {currentPage === 'register' && (
          <RegisterPage
            onRegisterSuccess={handleRegisterSuccess}
            onSwitchToLogin={() => setCurrentPage('login')}
          />
        )}
        {currentPage === 'dashboard' && (
          <Dashboard user={user} onLogout={handleLogout} />
        )}
      </div>
    </SettingsProvider>
  );
}

export default App;

