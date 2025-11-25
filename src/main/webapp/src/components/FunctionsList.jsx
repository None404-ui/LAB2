import React, { useState, useEffect } from 'react';
import { functionsAPI } from '../api/client';
import FunctionChart from './FunctionChart';
import { saveFunctionToFile } from '../utils/fileUtils';
import './FunctionsList.css';

function FunctionsList({ userId, refreshKey }) {
  const [functions, setFunctions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedFunction, setSelectedFunction] = useState(null);

  useEffect(() => {
    loadFunctions();
  }, [userId, refreshKey]);

  const loadFunctions = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Получаем userId из localStorage если не передан
      let targetUserId = userId;
      
      if (!targetUserId) {
        try {
          const userStr = localStorage.getItem('user');
          if (userStr) {
            const user = JSON.parse(userStr);
            targetUserId = user.userId;
          }
        } catch (e) {
          console.error('Error parsing user from localStorage:', e);
        }
      }
      
      if (!targetUserId) {
        // Если userId нет, показываем пустой список
        setFunctions([]);
        setLoading(false);
        return;
      }

      const response = await functionsAPI.getUserFunctions(targetUserId);
      console.log('Functions response:', response.data);
      if (response.data && response.data.success) {
        const funcs = response.data.data || [];
        console.log('Loaded functions:', funcs);
        if (funcs.length > 0) {
          console.log('First function xValues:', funcs[0].xValues);
          console.log('First function yValues:', funcs[0].yValues);
        }
        setFunctions(funcs);
      } else {
        setFunctions([]);
      }
    } catch (err) {
      console.error('Error loading functions:', err);
      setError('Ошибка загрузки функций');
      setFunctions([]);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="functions-list">
        <h2>Мои функции</h2>
        <p>Загрузка...</p>
      </div>
    );
  }

  return (
    <div className="functions-list">
      <h2>Мои функции</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      {functions.length === 0 ? (
        <div className="empty-state">
          <p>У вас пока нет созданных функций</p>
          <p>Создайте свою первую функцию используя кнопки выше!</p>
        </div>
      ) : (
        <div className="functions-grid">
          {functions.map((func) => (
            <div key={func.functionId} className="function-card">
              <div className="function-header">
                <h3>{func.name}</h3>
                <span className="function-type-badge">
                  {func.functionType === 'ARRAY' ? 'Array' : 'Linked List'}
                </span>
              </div>
              <div className="function-info">
                <p>
                  <strong>ID:</strong> {func.functionId}
                </p>
                <p>
                  <strong>Точек:</strong> {func.count || 0}
                </p>
                <p>
                  <strong>Диапазон:</strong> [{func.xValues?.[0]?.toFixed(2) || '?'}, {func.xValues?.[func.xValues?.length - 1]?.toFixed(2) || '?'}]
                </p>
              </div>
              <button
                className="btn btn-primary"
                onClick={() => {
                  if (func.xValues && func.yValues && func.xValues.length > 0) {
                    setSelectedFunction(func);
                  } else {
                    alert('Данные функции недоступны для отображения графика');
                  }
                }}
              >
                Показать график
              </button>
              <div style={{ display: 'flex', gap: '10px', marginTop: '10px', flexWrap: 'wrap' }}>
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    if (func.xValues && func.yValues) {
                      console.table(
                        func.xValues.map((x, i) => ({
                          'X': x,
                          'Y': func.yValues[i]
                        }))
                      );
                      alert('Точки функции выведены в консоль (F12)');
                    } else {
                      alert('Данные функции недоступны');
                    }
                  }}
                  style={{ flex: 1, minWidth: '80px' }}
                >
                  📋 Точки
                </button>
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    saveFunctionToFile(func);
                  }}
                  style={{ flex: 1, minWidth: '80px' }}
                  title="Сохранить функцию в JSON файл"
                >
                  💾 Скачать
                </button>
                <button
                  className="btn btn-danger"
                  onClick={async () => {
                    if (window.confirm(`Удалить функцию "${func.name}" (ID: ${func.functionId})?`)) {
                      try {
                        await functionsAPI.deleteFunction(func.functionId);
                        loadFunctions();
                      } catch (err) {
                        alert('Ошибка удаления: ' + (err.response?.data?.error || err.message));
                      }
                    }
                  }}
                  style={{ flex: 1, minWidth: '80px' }}
                  title="Удалить функцию"
                >
                  🗑️ Удалить
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedFunction && (
        <FunctionChart
          function={selectedFunction}
          onClose={() => setSelectedFunction(null)}
        />
      )}
    </div>
  );
}

export default FunctionsList;

