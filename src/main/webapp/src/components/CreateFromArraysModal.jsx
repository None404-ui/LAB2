import React, { useState } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import './Modal.css';

function CreateFromArraysModal({ onClose, onSuccess, returnFunction = false }) {
  const { factoryType } = useSettings();
  const [functionName, setFunctionName] = useState('');
  const [pointsCount, setPointsCount] = useState('');
  const [points, setPoints] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [tableCreated, setTableCreated] = useState(false);

  const handleCreateTable = () => {
    setError('');
    const count = parseInt(pointsCount);

    if (isNaN(count) || count < 2) {
      setError('Количество точек должно быть минимум 2');
      return;
    }

    if (count > 100) {
      setError('Количество точек не должно превышать 100 (для удобства)');
      return;
    }

    // Создаем массив точек
    const newPoints = Array.from({ length: count }, (_, i) => ({
      x: '',
      y: '',
    }));
    setPoints(newPoints);
    setTableCreated(true);
  };

  const handlePointChange = (index, field, value) => {
    const newPoints = [...points];
    newPoints[index][field] = value;
    setPoints(newPoints);
  };

  const validatePoints = () => {
    // Проверяем, что все поля заполнены
    for (let i = 0; i < points.length; i++) {
      if (points[i].x === '' || points[i].y === '') {
        throw new Error(`Заполните все значения (строка ${i + 1})`);
      }

      const x = parseFloat(points[i].x);
      const y = parseFloat(points[i].y);

      if (isNaN(x) || isNaN(y)) {
        throw new Error(`Некорректные числа в строке ${i + 1}`);
      }
    }

    // Проверяем сортировку x
    const xValues = points.map(p => parseFloat(p.x));
    for (let i = 1; i < xValues.length; i++) {
      if (xValues[i] <= xValues[i - 1]) {
        throw new Error('Значения X должны быть строго возрастающими');
      }
    }

    return {
      xValues,
      yValues: points.map(p => parseFloat(p.y)),
    };
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!functionName.trim()) {
        throw new Error('Введите название функции');
      }

      if (!tableCreated || points.length === 0) {
        throw new Error('Сначала создайте таблицу с точками');
      }

      const { xValues, yValues } = validatePoints();

      const response = await functionsAPI.createFromArrays(
        functionName,
        xValues,
        yValues,
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
          <h2>Создать функцию из массивов</h2>
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
              placeholder="Например: Моя функция"
              disabled={loading}
            />
          </div>

          <div style={{ marginBottom: '15px', fontSize: '14px', color: '#666' }}>
            Используется фабрика из настроек: <strong>{factoryType === 'ARRAY' ? 'Array' : 'Linked List'}</strong>
          </div>

          <div className="form-group">
            <label htmlFor="pointsCount">Количество точек</label>
            <div style={{ display: 'flex', gap: '10px' }}>
              <input
                id="pointsCount"
                type="number"
                value={pointsCount}
                onChange={(e) => setPointsCount(e.target.value)}
                placeholder="Введите количество"
                disabled={loading || tableCreated}
                min="2"
                max="100"
              />
              {!tableCreated && (
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={handleCreateTable}
                  disabled={loading}
                >
                  Создать таблицу
                </button>
              )}
              {tableCreated && (
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => {
                    setTableCreated(false);
                    setPoints([]);
                  }}
                  disabled={loading}
                >
                  Пересоздать
                </button>
              )}
            </div>
          </div>

          {tableCreated && (
            <div className="points-table-container">
              <table className="points-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>X</th>
                    <th>Y</th>
                  </tr>
                </thead>
                <tbody>
                  {points.map((point, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>
                        <input
                          type="number"
                          step="any"
                          value={point.x}
                          onChange={(e) =>
                            handlePointChange(index, 'x', e.target.value)
                          }
                          disabled={loading}
                          placeholder="X"
                        />
                      </td>
                      <td>
                        <input
                          type="number"
                          step="any"
                          value={point.y}
                          onChange={(e) =>
                            handlePointChange(index, 'y', e.target.value)
                          }
                          disabled={loading}
                          placeholder="Y"
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

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
              disabled={loading || !tableCreated}
            >
              {loading ? 'Создание...' : 'Создать функцию'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateFromArraysModal;

