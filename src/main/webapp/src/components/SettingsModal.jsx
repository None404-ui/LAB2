import React, { useState, useEffect } from 'react';
import { useSettings } from '../context/SettingsContext';
import './Modal.css';

function SettingsModal({ onClose }) {
  const { factoryType, setFactoryType, theme, setTheme } = useSettings();
  const [user, setUser] = useState(null);

  useEffect(() => {
    try {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        setUser(JSON.parse(userStr));
      }
    } catch (e) {
      console.error('Error loading user:', e);
    }
  }, []);

  const handleSave = () => {
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '500px' }}>
        <div className="modal-header">
          <h2>⚙️ Настройки</h2>
          <button className="modal-close" onClick={onClose}>
            ×
          </button>
        </div>

        <div className="settings-content">
          {/* Информация о пользователе */}
          {user && (
            <div className="setting-group" style={{ 
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
              color: 'white',
              borderRadius: '12px',
              padding: '20px',
              marginBottom: '20px'
            }}>
              <h3 style={{ margin: '0 0 15px 0', color: 'white' }}>👤 Информация о пользователе</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ opacity: 0.9 }}>User ID:</span>
                  <strong>{user.userId}</strong>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ opacity: 0.9 }}>Имя пользователя:</span>
                  <strong>{user.username}</strong>
                </div>
                {user.email && (
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ opacity: 0.9 }}>Email:</span>
                    <strong>{user.email}</strong>
                  </div>
                )}
                {user.roles && user.roles.length > 0 && (
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ opacity: 0.9 }}>Роли:</span>
                    <strong>{user.roles.join(', ')}</strong>
                  </div>
                )}
              </div>
            </div>
          )}

          <div className="setting-group">
            <h3>Тип фабрики по умолчанию</h3>
            <p className="setting-description">
              Выберите, какую реализацию использовать для создания новых табулированных функций
            </p>

            <div className="factory-options">
              <label className={`factory-option ${factoryType === 'ARRAY' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="factoryType"
                  value="ARRAY"
                  checked={factoryType === 'ARRAY'}
                  onChange={(e) => setFactoryType(e.target.value)}
                />
                <div className="factory-option-content">
                  <div className="factory-icon">📊</div>
                  <div>
                    <div className="factory-name">Array (Массив)</div>
                    <div className="factory-desc">
                      Быстрый доступ по индексу, фиксированный размер
                    </div>
                  </div>
                </div>
              </label>

              <label className={`factory-option ${factoryType === 'LINKED_LIST' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="factoryType"
                  value="LINKED_LIST"
                  checked={factoryType === 'LINKED_LIST'}
                  onChange={(e) => setFactoryType(e.target.value)}
                />
                <div className="factory-option-content">
                  <div className="factory-icon">🔗</div>
                  <div>
                    <div className="factory-name">Linked List (Связный список)</div>
                    <div className="factory-desc">
                      Эффективные вставки и удаления элементов
                    </div>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div className="setting-info">
            <strong>💡 Подсказка:</strong> Выбранная фабрика будет использоваться при создании новых функций
            из массивов и математических функций. Вы можете изменить её в любое время.
          </div>

          {/* Тема */}
          <div className="setting-group">
            <h3>Тема интерфейса</h3>
            <p className="setting-description">
              Выберите светлую или темную тему оформления
            </p>

            <div className="factory-options">
              <label className={`factory-option ${theme === 'light' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="theme"
                  value="light"
                  checked={theme === 'light'}
                  onChange={(e) => setTheme(e.target.value)}
                />
                <div className="factory-option-content">
                  <div className="factory-icon">☀️</div>
                  <div>
                    <div className="factory-name">Светлая тема</div>
                    <div className="factory-desc">
                      Классическое светлое оформление
                    </div>
                  </div>
                </div>
              </label>

              <label className={`factory-option ${theme === 'dark' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="theme"
                  value="dark"
                  checked={theme === 'dark'}
                  onChange={(e) => setTheme(e.target.value)}
                />
                <div className="factory-option-content">
                  <div className="factory-icon">🌙</div>
                  <div>
                    <div className="factory-name">Темная тема</div>
                    <div className="factory-desc">
                      Приятная темная тема для глаз
                    </div>
                  </div>
                </div>
              </label>
            </div>
          </div>
        </div>

        <div className="modal-actions">
          <button className="btn btn-primary" onClick={handleSave}>
            Сохранить
          </button>
        </div>
      </div>
    </div>
  );
}

export default SettingsModal;

