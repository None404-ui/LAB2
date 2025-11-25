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

function FunctionOperationsModal({ onClose, onSuccess }) {
  const { factoryType } = useSettings();
  const [function1, setFunction1] = useState(null);
  const [function2, setFunction2] = useState(null);
  const [result, setResult] = useState(null);
  const [resultName, setResultName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  // Модалки создания
  const [showCreateArrays1, setShowCreateArrays1] = useState(false);
  const [showCreateMath1, setShowCreateMath1] = useState(false);
  const [showCreateArrays2, setShowCreateArrays2] = useState(false);
  const [showCreateMath2, setShowCreateMath2] = useState(false);
  
  // Рефы для file input
  const fileInput1Ref = useRef(null);
  const fileInput2Ref = useRef(null);

  const loadFunctionById = async (functionId, setter) => {
    try {
      setError('');
      const response = await functionsAPI.getFunctionById(functionId);
      if (response.data.success) {
        setter(response.data.data);
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Ошибка загрузки функции');
    }
  };

  const handleFileLoad = async (file, setter) => {
    if (!file) return;
    
    setLoading(true);
    setError('');
    
    try {
      const data = await loadFunctionFromFile(file);
      const response = await createFunctionFromLoadedData(data, factoryType, functionsAPI);
      
      if (response.success) {
        setter(response.data);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const performOperation = async (operation, operationName) => {
    if (!function1 || !function2) {
      setError('Загрузите обе функции для выполнения операции');
      return;
    }

    if (!resultName.trim()) {
      setError('Введите название результата');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await functionsAPI.performOperation(
        function1.functionId,
        function2.functionId,
        operation,
        resultName,
        factoryType
      );

      if (response.data.success) {
        setResult(response.data.data);
        setError('');
      }
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || `Ошибка при ${operationName}`);
    } finally {
      setLoading(false);
    }
  };

  const handleFunctionCreated1 = (newFunc) => {
    setFunction1(newFunc);
    setShowCreateArrays1(false);
    setShowCreateMath1(false);
  };

  const handleFunctionCreated2 = (newFunc) => {
    setFunction2(newFunc);
    setShowCreateArrays2(false);
    setShowCreateMath2(false);
  };

  const saveResult = () => {
    if (result) {
      onSuccess();
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div 
        className="modal-content operations-modal" 
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <h2>🧮 Операции над функциями</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="operations-container">
          {/* Функция 1 */}
          <div className="function-panel">
            <h3>Функция 1</h3>
            <FunctionPanel
              func={function1}
              setFunc={setFunction1}
              onLoadById={(id) => loadFunctionById(id, setFunction1)}
              onLoadFromFile={(file) => handleFileLoad(file, setFunction1)}
              onCreateFromArrays={() => setShowCreateArrays1(true)}
              onCreateFromMath={() => setShowCreateMath1(true)}
              fileInputRef={fileInput1Ref}
              loading={loading}
            />
          </div>

          {/* Операции */}
          <div className="operations-panel">
            <h3>Операции</h3>
            <div className="operation-buttons">
              <button
                className="btn btn-operation"
                onClick={() => performOperation('ADD', 'сложении')}
                disabled={loading || !function1 || !function2}
              >
                ➕ Сложение
              </button>
              <button
                className="btn btn-operation"
                onClick={() => performOperation('SUBTRACT', 'вычитании')}
                disabled={loading || !function1 || !function2}
              >
                ➖ Вычитание
              </button>
              <button
                className="btn btn-operation"
                onClick={() => performOperation('MULTIPLY', 'умножении')}
                disabled={loading || !function1 || !function2}
              >
                ✖️ Умножение
              </button>
              <button
                className="btn btn-operation"
                onClick={() => performOperation('DIVIDE', 'делении')}
                disabled={loading || !function1 || !function2}
              >
                ➗ Деление
              </button>
            </div>

            <div className="form-group" style={{ marginTop: '20px' }}>
              <label>Название результата:</label>
              <input
                type="text"
                value={resultName}
                onChange={(e) => setResultName(e.target.value)}
                placeholder="Например: Сумма функций"
                disabled={loading}
              />
            </div>

            <div style={{ marginTop: '10px', fontSize: '12px', color: '#666' }}>
              Используется фабрика: <strong>{factoryType === 'ARRAY' ? 'Array' : 'Linked List'}</strong>
            </div>
          </div>

          {/* Функция 2 */}
          <div className="function-panel">
            <h3>Функция 2</h3>
            <FunctionPanel
              func={function2}
              setFunc={setFunction2}
              onLoadById={(id) => loadFunctionById(id, setFunction2)}
              onLoadFromFile={(file) => handleFileLoad(file, setFunction2)}
              onCreateFromArrays={() => setShowCreateArrays2(true)}
              onCreateFromMath={() => setShowCreateMath2(true)}
              fileInputRef={fileInput2Ref}
              loading={loading}
            />
          </div>
        </div>

        {/* Результат */}
        {result && (
          <div className="result-panel">
            <h3>✅ Результат: {result.name}</h3>
            <EditableTable func={result} editable={false} />
            <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
              <button 
                className="btn btn-secondary" 
                onClick={() => saveFunctionToFile(result)}
              >
                💾 Сохранить в файл
              </button>
              <button 
                className="btn btn-primary" 
                onClick={saveResult}
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

      {/* Модалки создания для функции 1 */}
      {showCreateArrays1 && (
        <CreateFromArraysModal
          onClose={() => setShowCreateArrays1(false)}
          onSuccess={handleFunctionCreated1}
          returnFunction={true}
        />
      )}
      {showCreateMath1 && (
        <CreateFromMathModal
          onClose={() => setShowCreateMath1(false)}
          onSuccess={handleFunctionCreated1}
          returnFunction={true}
        />
      )}

      {/* Модалки создания для функции 2 */}
      {showCreateArrays2 && (
        <CreateFromArraysModal
          onClose={() => setShowCreateArrays2(false)}
          onSuccess={handleFunctionCreated2}
          returnFunction={true}
        />
      )}
      {showCreateMath2 && (
        <CreateFromMathModal
          onClose={() => setShowCreateMath2(false)}
          onSuccess={handleFunctionCreated2}
          returnFunction={true}
        />
      )}
    </div>
  );
}

// Компонент панели функции с кнопками создать/загрузить/сохранить
function FunctionPanel({ func, setFunc, onLoadById, onLoadFromFile, onCreateFromArrays, onCreateFromMath, fileInputRef, loading }) {
  return (
    <div>
      {/* Кнопки действий */}
      <div className="function-actions-grid">
        <button 
          className="btn btn-sm btn-create"
          onClick={onCreateFromArrays}
          disabled={loading}
          title="Создать из массивов X и Y"
        >
          📊 Из массивов
        </button>
        <button 
          className="btn btn-sm btn-create"
          onClick={onCreateFromMath}
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
            onLoadFromFile(e.target.files[0]);
            e.target.value = '';
          }}
          style={{ display: 'none' }}
        />
      </div>

      {/* Загрузка по ID */}
      <div className="function-selector" style={{ marginTop: '10px' }}>
        <FunctionIdSelector
          onSelect={onLoadById}
          disabled={loading}
          placeholder="ID функции"
        />
      </div>

      {/* Отображение функции */}
      {func && (
        <div style={{ marginTop: '15px' }}>
          <div style={{ 
            background: '#f8f9ff', 
            padding: '10px', 
            borderRadius: '8px',
            marginBottom: '10px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <div>
              <strong>{func.name}</strong>
              <div style={{ fontSize: '11px', color: '#666' }}>
                ID: {func.functionId} | Точек: {func.count}
              </div>
            </div>
            <button
              className="btn btn-sm btn-secondary"
              onClick={() => saveFunctionToFile(func)}
              title="Сохранить в файл"
            >
              💾
            </button>
          </div>
          <EditableTable
            func={func}
            editable={true}
            onUpdate={(newFunc) => setFunc(newFunc)}
            isInsertable={func.isInsertable}
            isRemovable={func.isRemovable}
          />
        </div>
      )}
    </div>
  );
}

export default FunctionOperationsModal;
