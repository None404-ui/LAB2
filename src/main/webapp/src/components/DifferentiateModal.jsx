import React, { useState, useRef } from 'react';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';
import { saveFunctionToFile, loadFunctionFromFile, createFunctionFromLoadedData } from '../utils/fileUtils';
import EditableTable from './EditableTable';
import CreateFromArraysModal from './CreateFromArraysModal';
import CreateFromMathModal from './CreateFromMathModal';
import FunctionIdSelector from './FunctionIdSelector';
import './Modal.css';
import './OperationsModal.css';

function DifferentiateModal({ onClose, onSuccess }) {
  const { factoryType } = useSettings();
  const [sourceFunction, setSourceFunction] = useState(null);
  const [result, setResult] = useState(null);
  const [resultName, setResultName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  // Модалки создания
  const [showCreateArrays, setShowCreateArrays] = useState(false);
  const [showCreateMath, setShowCreateMath] = useState(false);
  
  // Реф для file input
  const fileInputRef = useRef(null);

  const loadFunction = async (id) => {
    try {
      setError('');
      const response = await functionsAPI.getFunctionById(id);
      if (response.data.success) {
        setSourceFunction(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Ошибка загрузки функции');
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
        setSourceFunction(response.data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFunctionCreated = (newFunc) => {
    setSourceFunction(newFunc);
    setShowCreateArrays(false);
    setShowCreateMath(false);
  };

  const performDifferentiation = async () => {
    if (!sourceFunction) {
      setError('Загрузите функцию для дифференцирования');
      return;
    }

    if (!resultName.trim()) {
      setError('Введите название результата');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await functionsAPI.differentiate(
        sourceFunction.functionId,
        resultName,
        factoryType
      );

      if (response.data.success) {
        setResult(response.data.data);
        setError('');
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Ошибка при дифференцировании');
    } finally {
      setLoading(false);
    }
  };

  const saveResult = () => {
    if (result) {
      onSuccess();
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-content differentiate-modal" 
        onClick={(e) => e.stopPropagation()}
        style={{ maxWidth: '1000px' }}
      >
        <div className="modal-header">
          <h2>📐 Дифференцирование функции</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="differentiate-container">
          {/* Левая панель - исходная функция */}
          <div className="function-panel">
            <h3>Исходная функция</h3>
            
            {/* Кнопки создания */}
            <div className="function-actions-grid">
              <button 
                className="btn btn-sm btn-create"
                onClick={() => setShowCreateArrays(true)}
                disabled={loading}
                title="Создать из массивов X и Y"
              >
                📊 Из массивов
              </button>
              <button 
                className="btn btn-sm btn-create"
                onClick={() => setShowCreateMath(true)}
                disabled={loading}
                title="Создать из математической функции"
              >
                📈 Из функции
              </button>
              <button 
                className="btn btn-sm btn-secondary"
                onClick={() => fileInputRef.current?.click()}
                disabled={loading}
                title="Загрузить из JSON файла"
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
            <div className="function-selector" style={{ marginTop: '10px', marginBottom: '15px' }}>
              <FunctionIdSelector
                onSelect={loadFunction}
                disabled={loading}
                placeholder="ID функции"
              />
            </div>

            {sourceFunction && (
              <>
                <div style={{ 
                  background: '#f8f9ff', 
                  padding: '10px', 
                  borderRadius: '8px',
                  marginBottom: '15px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}>
                  <div>
                    <strong>{sourceFunction.name}</strong>
                    <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
                      ID: {sourceFunction.functionId} | Точек: {sourceFunction.count} | 
                      Тип: {sourceFunction.functionType === 'ARRAY' ? 'Array' : 'Linked List'}
                    </div>
                  </div>
                  <button
                    className="btn btn-sm btn-secondary"
                    onClick={() => saveFunctionToFile(sourceFunction)}
                    title="Сохранить в файл"
                  >
                    💾
                  </button>
                </div>
                <EditableTable 
                  func={sourceFunction} 
                  editable={true}
                  onUpdate={(newFunc) => setSourceFunction(newFunc)}
                  isInsertable={sourceFunction.isInsertable}
                  isRemovable={sourceFunction.isRemovable}
                />
              </>
            )}
          </div>

          {/* Правая панель - результат */}
          <div className="function-panel">
            <h3>Производная</h3>

            <div className="form-group" style={{ marginBottom: '15px' }}>
              <label>Название результата:</label>
              <input
                type="text"
                value={resultName}
                onChange={(e) => setResultName(e.target.value)}
                placeholder="Например: Производная функции"
                disabled={loading}
              />
            </div>

            <div style={{ marginBottom: '15px', fontSize: '12px', color: '#666' }}>
              Используется фабрика: <strong>{factoryType === 'ARRAY' ? 'Array' : 'Linked List'}</strong>
            </div>

            <button
              className="btn btn-primary"
              onClick={performDifferentiation}
              disabled={loading || !sourceFunction}
              style={{ width: '100%', marginBottom: '15px' }}
            >
              {loading ? 'Вычисление...' : '📐 Продифференцировать'}
            </button>

            {result && (
              <>
                <div style={{ 
                  background: '#e8f5e9', 
                  padding: '10px', 
                  borderRadius: '8px',
                  marginBottom: '15px',
                  borderLeft: '4px solid #4caf50'
                }}>
                  <strong>✅ {result.name}</strong>
                  <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
                    ID: {result.functionId} | Точек: {result.count}
                  </div>
                </div>
                <EditableTable func={result} editable={false} />
                <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                  <button 
                    className="btn btn-secondary" 
                    onClick={() => saveFunctionToFile(result)}
                    style={{ flex: 1 }}
                  >
                    💾 Сохранить в файл
                  </button>
                  <button 
                    className="btn btn-primary" 
                    onClick={saveResult}
                    style={{ flex: 1 }}
                  >
                    Готово
                  </button>
                </div>
              </>
            )}
          </div>
        </div>

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

export default DifferentiateModal;
