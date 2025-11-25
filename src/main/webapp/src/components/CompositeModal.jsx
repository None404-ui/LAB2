import React, { useState, useEffect } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import './Modal.css';

function CompositeModal({ onClose, onSuccess }) {
  const { factoryType } = useSettings();
  const [name, setName] = useState('');
  const [innerFunction, setInnerFunction] = useState('');
  const [outerFunction, setOuterFunction] = useState('');
  const [availableFunctions, setAvailableFunctions] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  // Параметры для создания табулированной функции
  const [xFrom, setXFrom] = useState('0');
  const [xTo, setXTo] = useState('10');
  const [count, setCount] = useState('11');
  const [createdFunction, setCreatedFunction] = useState(null);

  useEffect(() => {
    loadAvailableFunctions();
  }, []);

  const loadAvailableFunctions = async () => {
    try {
      const response = await functionsAPI.getMathFunctions();
      if (response.data.success) {
        setAvailableFunctions(response.data.data);
        if (response.data.data.length > 0) {
          setInnerFunction(response.data.data[0]);
          setOuterFunction(response.data.data[0]);
        }
      }
    } catch (err) {
      setError('Ошибка загрузки списка функций');
    }
  };

  const handleCreate = async () => {
    if (!name.trim()) {
      setError('Введите название функции');
      return;
    }
    if (!innerFunction || !outerFunction) {
      setError('Выберите обе функции');
      return;
    }

    const xFromNum = parseFloat(xFrom);
    const xToNum = parseFloat(xTo);
    const countNum = parseInt(count);

    if (isNaN(xFromNum) || isNaN(xToNum) || isNaN(countNum)) {
      setError('Введите корректные числа для интервала');
      return;
    }

    if (xFromNum >= xToNum) {
      setError('Начало интервала должно быть меньше конца');
      return;
    }

    if (countNum < 2 || countNum > 1000) {
      setError('Количество точек должно быть от 2 до 1000');
      return;
    }

    setLoading(true);
    setError('');

    try {
      // Сначала создаём составную функцию (добавляем в список доступных)
      const compositeResponse = await functionsAPI.createCompositeFunction(name, innerFunction, outerFunction);
      
      if (compositeResponse.data.success) {
        // Теперь создаём табулированную функцию из составной
        const tabulatedResponse = await functionsAPI.createFromMath(
          name,
          name, // используем имя составной функции
          xFromNum,
          xToNum,
          countNum,
          factoryType
        );

        if (tabulatedResponse.data.success) {
          setCreatedFunction(tabulatedResponse.data.data);
          // Обновляем список доступных функций
          await loadAvailableFunctions();
          setError('');
        }
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Ошибка создания функции');
    } finally {
      setLoading(false);
    }
  };

  const handleDone = () => {
    if (onSuccess) onSuccess();
    onClose();
  };

  const resetForm = () => {
    setName('');
    setCreatedFunction(null);
    setXFrom('0');
    setXTo('10');
    setCount('11');
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-content" 
        onClick={(e) => e.stopPropagation()}
        style={{ maxWidth: '650px' }}
      >
        <div className="modal-header">
          <h2>🔗 Создание составной функции</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        {!createdFunction ? (
          <div style={{ marginBottom: '20px' }}>
            <p style={{ color: '#666', marginBottom: '20px' }}>
              Составная функция (композиция) f ∘ g означает f(g(x)).
              <br />
              Функция будет сохранена в вашем списке.
            </p>

            <div className="form-group">
              <label>Название функции:</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Например: Квадрат синуса"
                disabled={loading}
              />
            </div>

            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: '1fr auto 1fr', 
              gap: '15px', 
              alignItems: 'center',
              marginBottom: '20px'
            }}>
              <div className="form-group" style={{ margin: 0 }}>
                <label>Внешняя функция f:</label>
                <select
                  value={outerFunction}
                  onChange={(e) => setOuterFunction(e.target.value)}
                  disabled={loading}
                >
                  {availableFunctions.map((func) => (
                    <option key={func} value={func}>{func}</option>
                  ))}
                </select>
              </div>

              <div style={{ 
                fontSize: '24px', 
                fontWeight: 'bold', 
                color: '#667eea',
                paddingTop: '20px'
              }}>
                ∘
              </div>

              <div className="form-group" style={{ margin: 0 }}>
                <label>Внутренняя функция g:</label>
                <select
                  value={innerFunction}
                  onChange={(e) => setInnerFunction(e.target.value)}
                  disabled={loading}
                >
                  {availableFunctions.map((func) => (
                    <option key={func} value={func}>{func}</option>
                  ))}
                </select>
              </div>
            </div>

            {innerFunction && outerFunction && (
              <div style={{ 
                background: '#f8f9ff', 
                padding: '15px', 
                borderRadius: '8px',
                textAlign: 'center',
                marginBottom: '20px'
              }}>
                <div style={{ fontSize: '14px', color: '#666' }}>Результат:</div>
                <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#333' }}>
                  {name || '?'}(x) = {outerFunction}({innerFunction}(x))
                </div>
              </div>
            )}

            {/* Параметры табуляции */}
            <div style={{ 
              background: '#fff3e0', 
              padding: '15px', 
              borderRadius: '8px',
              marginBottom: '20px'
            }}>
              <h4 style={{ margin: '0 0 15px 0', color: '#e65100' }}>📊 Параметры табуляции</h4>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label style={{ fontSize: '12px' }}>X от:</label>
                  <input
                    type="number"
                    step="any"
                    value={xFrom}
                    onChange={(e) => setXFrom(e.target.value)}
                    disabled={loading}
                  />
                </div>
                <div className="form-group" style={{ margin: 0 }}>
                  <label style={{ fontSize: '12px' }}>X до:</label>
                  <input
                    type="number"
                    step="any"
                    value={xTo}
                    onChange={(e) => setXTo(e.target.value)}
                    disabled={loading}
                  />
                </div>
                <div className="form-group" style={{ margin: 0 }}>
                  <label style={{ fontSize: '12px' }}>Точек:</label>
                  <input
                    type="number"
                    min="2"
                    max="1000"
                    value={count}
                    onChange={(e) => setCount(e.target.value)}
                    disabled={loading}
                  />
                </div>
              </div>
              
              <div style={{ fontSize: '11px', color: '#666', marginTop: '10px' }}>
                Фабрика: <strong>{factoryType === 'ARRAY' ? 'Array' : 'Linked List'}</strong>
              </div>
            </div>

            <button
              className="btn btn-primary"
              onClick={handleCreate}
              disabled={loading || !name.trim()}
              style={{ width: '100%' }}
            >
              {loading ? 'Создание...' : '🔗 Создать и сохранить функцию'}
            </button>
          </div>
        ) : (
          <div style={{ marginBottom: '20px' }}>
            <div style={{ 
              background: '#e8f5e9', 
              padding: '20px', 
              borderRadius: '12px',
              borderLeft: '4px solid #4caf50',
              marginBottom: '20px'
            }}>
              <h3 style={{ color: '#2e7d32', margin: '0 0 15px 0' }}>✅ Функция создана!</h3>
              <div style={{ marginBottom: '10px' }}>
                <strong>{createdFunction.name}</strong>
              </div>
              <div style={{ fontSize: '13px', color: '#666' }}>
                <div>ID: {createdFunction.functionId}</div>
                <div>Точек: {createdFunction.count}</div>
                <div>Диапазон: [{createdFunction.xValues?.[0]?.toFixed(2)}, {createdFunction.xValues?.[createdFunction.xValues?.length - 1]?.toFixed(2)}]</div>
                <div>Тип: {createdFunction.functionType === 'ARRAY' ? 'Array' : 'Linked List'}</div>
              </div>
            </div>

            <p style={{ fontSize: '13px', color: '#666', marginBottom: '15px' }}>
              💡 Функция сохранена в вашем списке и доступна для использования.
              Также она добавлена в список математических функций для создания новых композиций.
            </p>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                className="btn btn-secondary"
                onClick={resetForm}
                style={{ flex: 1 }}
              >
                Создать ещё
              </button>
              <button
                className="btn btn-primary"
                onClick={handleDone}
                style={{ flex: 1 }}
              >
                Готово
              </button>
            </div>
          </div>
        )}

        <div className="modal-actions">
          <button className="btn btn-secondary" onClick={onClose}>
            Закрыть
          </button>
        </div>
      </div>
    </div>
  );
}

export default CompositeModal;
