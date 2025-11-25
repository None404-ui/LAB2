import React, { useState, useRef, useEffect } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import { loadFunctionFromFile, createFunctionFromLoadedData } from '../utils/fileUtils';
import CreateFromArraysModal from './CreateFromArraysModal';
import CreateFromMathModal from './CreateFromMathModal';
import FunctionIdSelector from './FunctionIdSelector';
import './Modal.css';
import './OperationsModal.css';

function IntegralModal({ onClose }) {
  const { factoryType } = useSettings();
  const [selectedFunction, setSelectedFunction] = useState(null);
  const [threadCount, setThreadCount] = useState(4);
  const [maxThreads, setMaxThreads] = useState(16);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const [showCreateArrays, setShowCreateArrays] = useState(false);
  const [showCreateMath, setShowCreateMath] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    // Получаем максимальное количество потоков
    functionsAPI.getMaxThreads()
      .then(response => {
        if (response.data.success) {
          setMaxThreads(response.data.data);
        }
      })
      .catch(() => {});
  }, []);

  const loadFunction = async (id) => {
    try {
      setError('');
      setLoading(true);
      const response = await functionsAPI.getFunctionById(id);
      if (response.data.success) {
        setSelectedFunction(response.data.data);
        setResult(null);
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Ошибка загрузки функции');
    } finally {
      setLoading(false);
    }
  };

  const handleFileLoad = async (file) => {
    if (!file) return;
    
    setLoading(true);
    setError('');
    
    try {
      const data = await loadFunctionFromFile(file);
      const response = await createFunctionFromLoadedData(data, factoryType, functionsAPI);
      
      if (response.success) {
        setSelectedFunction(response.data);
        setResult(null);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFunctionCreated = (newFunc) => {
    setSelectedFunction(newFunc);
    setShowCreateArrays(false);
    setShowCreateMath(false);
    setResult(null);
  };

  const calculateIntegral = async () => {
    if (!selectedFunction) {
      setError('Выберите функцию');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await functionsAPI.calculateIntegral(selectedFunction.functionId, threadCount);
      if (response.data.success) {
        setResult(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Ошибка вычисления интеграла');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-content" 
        onClick={(e) => e.stopPropagation()}
        style={{ maxWidth: '700px' }}
      >
        <div className="modal-header">
          <h2>∫ Вычисление интеграла</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ marginBottom: '15px' }}>Выберите функцию</h3>
          
          {/* Кнопки создания */}
          <div className="function-actions-grid" style={{ marginBottom: '15px' }}>
            <button 
              className="btn btn-sm btn-create"
              onClick={() => setShowCreateArrays(true)}
              disabled={loading}
            >
              📊 Из массивов
            </button>
            <button 
              className="btn btn-sm btn-create"
              onClick={() => setShowCreateMath(true)}
              disabled={loading}
            >
              📈 Из функции
            </button>
            <button 
              className="btn btn-sm btn-secondary"
              onClick={() => fileInputRef.current?.click()}
              disabled={loading}
            >
              📁 Из файла
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept=".json"
              onChange={(e) => {
                handleFileLoad(e.target.files[0]);
                e.target.value = '';
              }}
              style={{ display: 'none' }}
            />
          </div>

          {/* Загрузка по ID */}
          <div style={{ marginBottom: '15px' }}>
            <FunctionIdSelector
              onSelect={loadFunction}
              disabled={loading}
              placeholder="ID функции"
            />
          </div>

          {/* Выбранная функция */}
          {selectedFunction && (
            <div style={{ 
              background: '#f8f9ff', 
              padding: '15px', 
              borderRadius: '8px',
              marginBottom: '20px'
            }}>
              <strong>{selectedFunction.name}</strong>
              <div style={{ fontSize: '13px', color: '#666', marginTop: '5px' }}>
                ID: {selectedFunction.functionId} | 
                Точек: {selectedFunction.count} | 
                Диапазон: [{selectedFunction.xValues?.[0]?.toFixed(2)}, {selectedFunction.xValues?.[selectedFunction.xValues?.length - 1]?.toFixed(2)}]
              </div>
            </div>
          )}
        </div>

        {/* Настройки вычисления */}
        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ marginBottom: '15px' }}>Параметры вычисления</h3>
          
          <div className="form-group">
            <label>Количество потоков: <strong>{threadCount}</strong></label>
            <input
              type="range"
              min={1}
              max={maxThreads}
              step={1}
              value={threadCount}
              onChange={(e) => setThreadCount(parseInt(e.target.value))}
              className="thread-slider"
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#666', marginTop: '5px' }}>
              <span>1</span>
              <span>{maxThreads}</span>
            </div>
          </div>

          <button
            className="btn btn-primary"
            onClick={calculateIntegral}
            disabled={loading || !selectedFunction}
            style={{ width: '100%', marginTop: '15px' }}
          >
            {loading ? 'Вычисление...' : '∫ Вычислить интеграл'}
          </button>
        </div>

        {/* Результат */}
        {result && (
          <div style={{ 
            background: '#e8f5e9', 
            padding: '20px', 
            borderRadius: '12px',
            borderLeft: '4px solid #4caf50'
          }}>
            <h3 style={{ color: '#2e7d32', marginBottom: '15px' }}>✅ Результат</h3>
            <div style={{ fontSize: '24px', fontWeight: 'bold', marginBottom: '15px' }}>
              ∫ = {result.result?.toFixed(6)}
            </div>
            <div style={{ fontSize: '13px', color: '#666' }}>
              <div>Функция: {result.functionName}</div>
              <div>Интервал: [{result.from?.toFixed(2)}, {result.to?.toFixed(2)}]</div>
              <div>Потоков: {result.threadCount}</div>
              <div>Время вычисления: {result.timeMs} мс</div>
            </div>
          </div>
        )}

        <div className="modal-actions">
          <button className="btn btn-secondary" onClick={onClose}>
            Закрыть
          </button>
        </div>
      </div>

      {/* Модалки создания */}
      {showCreateArrays && (
        <CreateFromArraysModal
          onClose={() => setShowCreateArrays(false)}
          onSuccess={handleFunctionCreated}
          returnFunction={true}
        />
      )}
      {showCreateMath && (
        <CreateFromMathModal
          onClose={() => setShowCreateMath(false)}
          onSuccess={handleFunctionCreated}
          returnFunction={true}
        />
      )}
    </div>
  );
}

export default IntegralModal;

