import React, { useState, useEffect, useRef } from 'react';
import { functionsAPI } from '../api/client';

/**
 * Компонент для выбора функции по ID с возможностью:
 * - Ввести ID вручную
 * - Развернуть список своих функций и выбрать из него
 */
function FunctionIdSelector({ onSelect, disabled = false, placeholder = "ID функции" }) {
  const [inputValue, setInputValue] = useState('');
  const [showDropdown, setShowDropdown] = useState(false);
  const [userFunctions, setUserFunctions] = useState([]);
  const [loading, setLoading] = useState(false);
  const containerRef = useRef(null);

  // Загружаем функции пользователя при открытии списка
  const loadUserFunctions = async () => {
    if (userFunctions.length > 0) return; // Уже загружены
    
    setLoading(true);
    try {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        const user = JSON.parse(userStr);
        const response = await functionsAPI.getUserFunctions(user.userId);
        if (response.data.success) {
          setUserFunctions(response.data.data || []);
        }
      }
    } catch (err) {
      console.error('Error loading user functions:', err);
    } finally {
      setLoading(false);
    }
  };

  // Закрываем список при клике вне компонента
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleToggleDropdown = () => {
    if (!showDropdown) {
      loadUserFunctions();
    }
    setShowDropdown(!showDropdown);
  };

  const handleSelectFunction = (func) => {
    setInputValue(func.functionId.toString());
    setShowDropdown(false);
    onSelect(func.functionId);
  };

  const handleInputChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleLoadClick = () => {
    const id = parseInt(inputValue);
    if (!isNaN(id) && id > 0) {
      onSelect(id);
      setInputValue('');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleLoadClick();
    }
  };

  return (
    <div ref={containerRef} style={{ position: 'relative' }}>
      <div style={{ display: 'flex', gap: '8px' }}>
        <div style={{ flex: 1, position: 'relative', display: 'flex' }}>
          <input
            type="text"
            inputMode="numeric"
            pattern="[0-9]*"
            value={inputValue}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            placeholder={placeholder}
            disabled={disabled}
            style={{ 
              flex: 1,
              borderRadius: '10px 0 0 10px',
              borderRight: 'none',
              minWidth: 0
            }}
          />
          <button
            type="button"
            onClick={handleToggleDropdown}
            disabled={disabled}
            style={{
              background: '#f5f5f5',
              border: '2px solid #e0e0e0',
              borderLeft: 'none',
              borderRadius: '0 10px 10px 0',
              cursor: disabled ? 'not-allowed' : 'pointer',
              padding: '0 12px',
              fontSize: '14px',
              color: '#666',
              opacity: disabled ? 0.5 : 1,
              display: 'flex',
              alignItems: 'center',
              transition: 'background 0.2s'
            }}
            title="Показать список функций"
            onMouseEnter={(e) => { if (!disabled) e.target.style.background = '#e8e8e8'; }}
            onMouseLeave={(e) => { e.target.style.background = '#f5f5f5'; }}
          >
            {showDropdown ? '▲' : '▼'}
          </button>
        </div>
        <button 
          className="btn btn-secondary" 
          onClick={handleLoadClick}
          disabled={!inputValue || disabled}
          style={{ whiteSpace: 'nowrap', padding: '10px 16px' }}
        >
          Загрузить
        </button>
      </div>

      {/* Выпадающий список */}
      {showDropdown && (
        <div style={{
          position: 'absolute',
          top: '100%',
          left: 0,
          right: 0,
          marginTop: '5px',
          background: 'white',
          border: '2px solid #e0e0e0',
          borderRadius: '10px',
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
          zIndex: 1000,
          maxHeight: '250px',
          overflowY: 'auto'
        }}>
          {loading ? (
            <div style={{ padding: '15px', textAlign: 'center', color: '#666' }}>
              Загрузка...
            </div>
          ) : userFunctions.length === 0 ? (
            <div style={{ padding: '15px', textAlign: 'center', color: '#666' }}>
              У вас нет созданных функций
            </div>
          ) : (
            userFunctions.map((func, index) => (
              <div
                key={func.functionId}
                onClick={() => handleSelectFunction(func)}
                className="function-selector-item"
                style={{
                  padding: '12px 15px',
                  cursor: 'pointer',
                  borderBottom: index < userFunctions.length - 1 ? '1px solid #f0f0f0' : 'none',
                  background: 'white',
                  transition: 'background 0.2s'
                }}
                onMouseEnter={(e) => { e.currentTarget.style.background = '#f8f9ff'; }}
                onMouseLeave={(e) => { e.currentTarget.style.background = 'white'; }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div>
                    <div style={{ fontWeight: '600', color: '#333' }}>{func.name}</div>
                    <div style={{ fontSize: '11px', color: '#888', marginTop: '3px' }}>
                      Точек: {func.count} | Диапазон: [{func.xValues?.[0]?.toFixed(1) || '?'}, {func.xValues?.[func.xValues?.length - 1]?.toFixed(1) || '?'}]
                    </div>
                  </div>
                  <div style={{ 
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
                    color: 'white', 
                    padding: '4px 10px', 
                    borderRadius: '12px',
                    fontSize: '12px',
                    fontWeight: 'bold',
                    marginLeft: '10px'
                  }}>
                    #{func.functionId}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

export default FunctionIdSelector;

