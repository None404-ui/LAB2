import React, { useState } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import { saveFunctionToFile, loadFunctionFromFile, createFunctionFromLoadedData } from '../utils/fileUtils';
import FunctionChart from './FunctionChart';
import EditableTable from './EditableTable';
import FunctionIdSelector from './FunctionIdSelector';
import './Modal.css';
import './FunctionExplorer.css';

function FunctionExplorer({ onClose, onSuccess }) {
  const { factoryType } = useSettings();
  const [func, setFunc] = useState(null);
  const [applyX, setApplyX] = useState('');
  const [applyResult, setApplyResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showChart, setShowChart] = useState(false);
  const [editingName, setEditingName] = useState(false);
  const [newName, setNewName] = useState('');

  const loadFunction = async (id) => {
    try {
      setError('');
      setLoading(true);
      const response = await functionsAPI.getFunctionById(id);
      if (response.data.success) {
        setFunc(response.data.data);
        setApplyResult(null);
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Ошибка загрузки функции');
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async () => {
    if (!func) {
      setError('Загрузите функцию');
      return;
    }

    const x = parseFloat(applyX);
    if (isNaN(x)) {
      setError('Введите корректное значение X');
      return;
    }

    try {
      setError('');
      setLoading(true);
      const response = await functionsAPI.applyFunction(func.functionId, x);
      if (response.data.success) {
        setApplyResult(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка вычисления');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = () => {
    if (func) {
      saveFunctionToFile(func);
    }
  };

  const handleLoad = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    setLoading(true);
    setError('');

    try {
      const data = await loadFunctionFromFile(file);
      const response = await createFunctionFromLoadedData(data, factoryType, functionsAPI);
      
      if (response.success) {
        setFunc(response.data);
        setApplyResult(null);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
      event.target.value = '';
    }
  };

  const handleCreate = () => {
    onClose();
    // Здесь можно открыть модалку создания
  };

  const handleUpdateName = async () => {
    if (!newName.trim()) {
      setError('Имя не может быть пустым');
      return;
    }

    try {
      setError('');
      setLoading(true);
      const response = await functionsAPI.updateName(func.functionId, newName.trim());
      if (response.data.success) {
        setFunc(response.data.data);
        setEditingName(false);
        setNewName('');
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Ошибка изменения имени');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-content function-explorer" 
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <h2>🔍 Изучение функции</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        {!func ? (
          <div className="function-loader">
            <h3>Загрузите или создайте функцию</h3>
            
            <div className="load-options">
              <div className="load-option">
                <label>Загрузить по ID:</label>
                <FunctionIdSelector
                  onSelect={loadFunction}
                  disabled={loading}
                  placeholder="ID функции"
                />
              </div>

              <div className="load-option">
                <label>Загрузить из файла:</label>
                <input
                  type="file"
                  accept=".json"
                  onChange={handleLoad}
                  disabled={loading}
                />
              </div>

              <div className="load-option">
                <button 
                  className="btn btn-secondary" 
                  onClick={handleCreate}
                  disabled={loading}
                  style={{ width: '100%' }}
                >
                  Создать новую функцию
                </button>
              </div>
            </div>
          </div>
        ) : (
          <div className="function-content">
            {/* Информация о функции */}
            <div className="function-info-panel">
              {editingName ? (
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '10px' }}>
                  <input
                    type="text"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                    placeholder="Новое имя"
                    style={{ 
                      flex: 1, 
                      padding: '8px 12px', 
                      borderRadius: '6px', 
                      border: 'none',
                      fontSize: '16px'
                    }}
                    disabled={loading}
                  />
                  <button 
                    className="btn btn-primary" 
                    onClick={handleUpdateName}
                    disabled={loading}
                    style={{ padding: '8px 16px', margin: 0 }}
                  >
                    ✓
                  </button>
                  <button 
                    className="btn btn-secondary" 
                    onClick={() => { setEditingName(false); setNewName(''); }}
                    disabled={loading}
                    style={{ padding: '8px 16px', margin: 0 }}
                  >
                    ✕
                  </button>
                </div>
              ) : (
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '10px' }}>
                  <h3 style={{ margin: 0 }}>{func.name}</h3>
                  <button
                    onClick={() => { setEditingName(true); setNewName(func.name); }}
                    style={{ 
                      background: 'rgba(255,255,255,0.2)', 
                      border: 'none', 
                      borderRadius: '4px',
                      padding: '4px 8px',
                      cursor: 'pointer',
                      color: 'white'
                    }}
                    title="Изменить имя"
                  >
                    ✏️
                  </button>
                </div>
              )}
              <div className="info-badges">
                <span className="badge">ID: {func.functionId}</span>
                <span className="badge">
                  Тип: {func.functionType === 'ARRAY' ? 'Array' : 'Linked List'}
                </span>
                <span className="badge">Точек: {func.count || func.xValues?.length || 0}</span>
                <span className="badge">
                  X: [{func.xValues?.[0]?.toFixed?.(2) || '?'}, {func.xValues?.[func.xValues?.length - 1]?.toFixed?.(2) || '?'}]
                </span>
              </div>
            </div>

            {/* Кнопки действий */}
            <div className="function-actions">
              <button className="btn btn-secondary" onClick={handleSave}>
                💾 Сохранить
              </button>
              <button 
                className="btn btn-secondary" 
                onClick={() => setShowChart(!showChart)}
              >
                {showChart ? '📋 Таблица' : '📊 График'}
              </button>
              <button 
                className="btn btn-secondary" 
                onClick={() => setFunc(null)}
              >
                🔄 Загрузить другую
              </button>
            </div>

            {/* График или таблица */}
            {showChart ? (
              <div className="chart-container">
                <FunctionChart function={func} onClose={() => {}} inline />
              </div>
            ) : (
              <EditableTable
                func={func}
                editable={true}
                onUpdate={(updated) => setFunc(updated)}
                isInsertable={func.isInsertable}
                isRemovable={func.isRemovable}
              />
            )}

            {/* Вычисление apply(x) */}
            <div className="apply-section">
              <h3>Вычислить значение функции</h3>
              <div className="apply-controls">
                <div className="form-group">
                  <label>Введите X:</label>
                  <input
                    type="number"
                    step="any"
                    value={applyX}
                    onChange={(e) => setApplyX(e.target.value)}
                    placeholder="Например: 2.5"
                    disabled={loading}
                  />
                </div>
                <button
                  className="btn btn-primary"
                  onClick={handleApply}
                  disabled={loading || !applyX}
                >
                  {loading ? 'Вычисление...' : 'Вычислить'}
                </button>
              </div>

              {applyResult && (
                <div className="apply-result">
                  <strong>Результат:</strong> f({applyResult.x}) = {applyResult.y.toFixed(6)}
                </div>
              )}
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

export default FunctionExplorer;

