import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './Modal.css';

function FunctionChart({ function: func, onClose, inline = false }) {
  // Проверяем что данные существуют
  if (!func || !func.xValues || !func.yValues || func.xValues.length === 0) {
    if (inline) {
      return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>Данные функции недоступны</div>;
    }
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h2>Ошибка</h2>
            <button className="modal-close" onClick={onClose}>×</button>
          </div>
          <p>Данные функции недоступны или повреждены</p>
          <div className="modal-actions">
            <button className="btn btn-primary" onClick={onClose}>Закрыть</button>
          </div>
        </div>
      </div>
    );
  }

  // Подготовка данных для графика
  const chartData = func.xValues.map((x, index) => ({
    x: x,
    y: func.yValues[index],
  }));

  // Inline режим - без модального окна
  if (inline) {
    return (
      <div>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              dataKey="x" 
              label={{ value: 'X', position: 'insideBottom', offset: -5 }}
              type="number"
              domain={['dataMin', 'dataMax']}
            />
            <YAxis 
              label={{ value: 'Y', angle: -90, position: 'insideLeft' }}
              type="number"
              domain={['auto', 'auto']}
            />
            <Tooltip 
              formatter={(value) => typeof value === 'number' ? value.toFixed(4) : value}
              labelFormatter={(label) => `X: ${typeof label === 'number' ? label.toFixed(4) : label}`}
            />
            <Legend />
            <Line 
              type="monotone" 
              dataKey="y" 
              stroke="#667eea" 
              strokeWidth={2}
              dot={{ fill: '#764ba2', r: 4 }}
              activeDot={{ r: 6 }}
              name={func.name}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  }

  // Модальный режим
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '900px' }}>
        <div className="modal-header">
          <h2>График функции: {func.name}</h2>
          <button className="modal-close" onClick={onClose}>
            ×
          </button>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <p style={{ color: '#666', fontSize: '14px' }}>
            <strong>Тип:</strong> {func.functionType === 'ARRAY' ? 'Array' : 'Linked List'} | 
            <strong> Точек:</strong> {func.count || func.xValues?.length || 0} | 
            <strong> Диапазон:</strong> [{func.xValues?.[0]?.toFixed?.(2) || '?'}, {func.xValues?.[func.xValues?.length - 1]?.toFixed?.(2) || '?'}]
          </p>
        </div>

        <ResponsiveContainer width="100%" height={400}>
          <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              dataKey="x" 
              label={{ value: 'X', position: 'insideBottom', offset: -5 }}
              type="number"
              domain={['dataMin', 'dataMax']}
            />
            <YAxis 
              label={{ value: 'Y', angle: -90, position: 'insideLeft' }}
              type="number"
              domain={['auto', 'auto']}
            />
            <Tooltip 
              formatter={(value) => typeof value === 'number' ? value.toFixed(4) : value}
              labelFormatter={(label) => `X: ${typeof label === 'number' ? label.toFixed(4) : label}`}
            />
            <Legend />
            <Line 
              type="monotone" 
              dataKey="y" 
              stroke="#667eea" 
              strokeWidth={2}
              dot={{ fill: '#764ba2', r: 4 }}
              activeDot={{ r: 6 }}
              name={func.name}
            />
          </LineChart>
        </ResponsiveContainer>

        <div style={{ marginTop: '20px' }}>
          <details>
            <summary style={{ cursor: 'pointer', fontWeight: 600, marginBottom: '10px' }}>
              Показать таблицу точек
            </summary>
            <div className="points-table-container" style={{ maxHeight: '300px' }}>
              <table className="points-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>X</th>
                    <th>Y</th>
                  </tr>
                </thead>
                <tbody>
                  {func.xValues?.map((x, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>{typeof x === 'number' ? x.toFixed(6) : x}</td>
                      <td>{typeof func.yValues?.[index] === 'number' ? func.yValues[index].toFixed(6) : func.yValues?.[index]}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </details>
        </div>

        <div className="modal-actions">
          <button className="btn btn-primary" onClick={onClose}>
            Закрыть
          </button>
        </div>
      </div>
    </div>
  );
}

export default FunctionChart;



