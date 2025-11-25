import React, { useState, useEffect } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import './Modal.css';

function CreateFromMathModal({ onClose, onSuccess, returnFunction = false }) {
  const { factoryType } = useSettings();
  const [functionName, setFunctionName] = useState('');
  const [mathFunctionType, setMathFunctionType] = useState('');
  const [xFrom, setXFrom] = useState('');
  const [xTo, setXTo] = useState('');
  const [count, setCount] = useState('');
  const [availableFunctions, setAvailableFunctions] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Загружаем список доступных функций
    const loadFunctions = async () => {
      try {
        const response = await functionsAPI.getMathFunctions();
        if (response.data.success) {
          setAvailableFunctions(response.data.data);
          if (response.data.data.length > 0) {
            setMathFunctionType(response.data.data[0]);
          }
        }
      } catch (err) {
        setError('Ошибка загрузки списка функций');
      }
    };
    loadFunctions();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!functionName.trim()) {
        throw new Error('Введите название функции');
      }

      if (!mathFunctionType) {
        throw new Error('Выберите математическую функцию');
      }

      const xFromNum = parseFloat(xFrom);
      const xToNum = parseFloat(xTo);
      const countNum = parseInt(count);

      if (isNaN(xFromNum) || isNaN(xToNum) || isNaN(countNum)) {
        throw new Error('Все поля должны быть заполнены корректными числами');
      }

      if (xFromNum >= xToNum) {
        throw new Error('Начало интервала должно быть меньше конца');
      }

      if (countNum < 2) {
        throw new Error('Количество точек должно быть минимум 2');
      }

      if (countNum > 1000) {
        throw new Error('Количество точек не должно превышать 1000');
      }

      const response = await functionsAPI.createFromMath(
        functionName,
        mathFunctionType,
        xFromNum,
        xToNum,
        countNum,
        factoryType
      );

      if (response.data.success) {
        if (returnFunction) {
          // Возвращаем созданную функцию в родительский компонент
          onSuccess(response.data.data);
        } else {
          onSuccess();
        }
      }
    } catch (err) {
      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        err.message ||
        'Ошибка создания функции'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Создать функцию из математической функции</h2>
          <button className="modal-close" onClick={onClose}>
            ×
          </button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="functionName">Название функции</label>
            <input
              id="functionName"
              type="text"
              value={functionName}
              onChange={(e) => setFunctionName(e.target.value)}
              placeholder="Например: Квадратичная с шагом 0.1"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="mathFunction">Математическая функция</label>
            <select
              id="mathFunction"
              value={mathFunctionType}
              onChange={(e) => setMathFunctionType(e.target.value)}
              disabled={loading}
            >
              {availableFunctions.map((func) => (
                <option key={func} value={func}>
                  {func}
                </option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '15px', fontSize: '14px', color: '#666' }}>
            Используется фабрика из настроек: <strong>{factoryType === 'ARRAY' ? 'Array' : 'Linked List'}</strong>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="xFrom">Начало интервала (X от)</label>
              <input
                id="xFrom"
                type="number"
                step="any"
                value={xFrom}
                onChange={(e) => setXFrom(e.target.value)}
                placeholder="Например: 0"
                disabled={loading}
                style={{
                  borderColor: xFrom && xTo && parseFloat(xFrom) >= parseFloat(xTo) ? '#dc3545' : undefined
                }}
              />
            </div>

            <div className="form-group">
              <label htmlFor="xTo">Конец интервала (X до)</label>
              <input
                id="xTo"
                type="number"
                step="any"
                value={xTo}
                onChange={(e) => setXTo(e.target.value)}
                placeholder="Например: 10"
                disabled={loading}
                style={{
                  borderColor: xFrom && xTo && parseFloat(xFrom) >= parseFloat(xTo) ? '#dc3545' : undefined
                }}
              />
            </div>
          </div>
          {xFrom && xTo && parseFloat(xFrom) >= parseFloat(xTo) && (
            <div style={{ color: '#dc3545', fontSize: '12px', marginTop: '-10px', marginBottom: '10px' }}>
              ⚠️ Начало интервала должно быть строго меньше конца!
            </div>
          )}

          <div className="form-group">
            <label htmlFor="count">Количество точек разбиения</label>
            <input
              id="count"
              type="number"
              value={count}
              onChange={(e) => setCount(e.target.value)}
              placeholder="Например: 11"
              disabled={loading}
              min="2"
              max="1000"
            />
            <small style={{ color: '#666', fontSize: '12px', marginTop: '5px', display: 'block' }}>
              Количество точек, в которых будет вычислена функция (от 2 до 1000)
            </small>
          </div>

          <div className="modal-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Отмена
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Создание...' : 'Создать функцию'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateFromMathModal;

