import React, { useState, useRef } from 'react';
import CreateFromArraysModal from './CreateFromArraysModal';
import CreateFromMathModal from './CreateFromMathModal';
import SettingsModal from './SettingsModal';
import FunctionOperationsModal from './FunctionOperationsModal';
import DifferentiateModal from './DifferentiateModal';
import FunctionExplorer from './FunctionExplorer';
import IntegralModal from './IntegralModal';
import CompositeModal from './CompositeModal';
import FunctionsList from './FunctionsList';
import { loadFunctionFromFile, createFunctionFromLoadedData } from '../utils/fileUtils';
import { functionsAPI } from '../api/client';
import { useSettings } from '../context/SettingsContext';

function Dashboard({ user, onLogout }) {
  const { factoryType } = useSettings();
  const fileInputRef = useRef(null);
  const [showArraysModal, setShowArraysModal] = useState(false);
  const [showMathModal, setShowMathModal] = useState(false);
  const [showSettingsModal, setShowSettingsModal] = useState(false);
  const [showOperationsModal, setShowOperationsModal] = useState(false);
  const [showDifferentiateModal, setShowDifferentiateModal] = useState(false);
  const [showExplorer, setShowExplorer] = useState(false);
  const [showIntegralModal, setShowIntegralModal] = useState(false);
  const [showCompositeModal, setShowCompositeModal] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFunctionCreated = () => {
    setShowArraysModal(false);
    setShowMathModal(false);
    setShowOperationsModal(false);
    setShowDifferentiateModal(false);
    setShowExplorer(false);
    setShowIntegralModal(false);
    setShowCompositeModal(false);
    setRefreshKey(prev => prev + 1);
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    setLoading(true);
    setError('');

    try {
      const data = await loadFunctionFromFile(file);
      const response = await createFunctionFromLoadedData(data, factoryType, functionsAPI);
      
      if (response.success) {
        setRefreshKey(prev => prev + 1);
        alert(`Функция "${data.name}" успешно загружена!`);
      }
    } catch (err) {
      setError(err.message);
      alert(`Ошибка: ${err.message}`);
    } finally {
      setLoading(false);
      event.target.value = '';
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div>
          <h1>Табулированные функции</h1>
          <p>Добро пожаловать, {user?.username || 'Пользователь'}!</p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button 
            className="btn-settings" 
            onClick={() => setShowSettingsModal(true)}
            title="Настройки"
          >
            ⚙️ Настройки
          </button>
          <button className="btn-logout" onClick={onLogout}>
            Выйти
          </button>
        </div>
      </div>

      {error && <div className="error-message" style={{ marginBottom: '20px' }}>{error}</div>}

      <div className="dashboard-content">
        <div className="card" onClick={() => setShowArraysModal(true)} style={{ cursor: 'pointer' }}>
          <h2>📊 Создать из массивов</h2>
          <p>Создайте функцию, указав массивы значений X и Y</p>
          <button className="btn btn-primary">Создать</button>
        </div>

        <div className="card" onClick={() => setShowMathModal(true)} style={{ cursor: 'pointer' }}>
          <h2>📈 Создать из функции</h2>
          <p>Создайте функцию на основе математической функции</p>
          <button className="btn btn-primary">Создать</button>
        </div>

        <div className="card" onClick={() => fileInputRef.current?.click()} style={{ cursor: 'pointer' }}>
          <h2>📁 Загрузить из файла</h2>
          <p>Загрузите функцию из JSON файла</p>
          <button className="btn btn-primary" disabled={loading}>
            {loading ? 'Загрузка...' : 'Выбрать файл'}
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept=".json"
            onChange={handleFileUpload}
            style={{ display: 'none' }}
          />
        </div>

        <div className="card" onClick={() => setShowOperationsModal(true)} style={{ cursor: 'pointer' }}>
          <h2>🧮 Операции над функциями</h2>
          <p>Сложение, вычитание, умножение и деление функций</p>
          <button className="btn btn-primary">Открыть</button>
        </div>

        <div className="card" onClick={() => setShowDifferentiateModal(true)} style={{ cursor: 'pointer' }}>
          <h2>📐 Дифференцирование</h2>
          <p>Вычислите производную табулированной функции</p>
          <button className="btn btn-primary">Открыть</button>
        </div>

        <div className="card" onClick={() => setShowExplorer(true)} style={{ cursor: 'pointer' }}>
          <h2>🔍 Изучение функции</h2>
          <p>Изучайте функцию, вычисляйте значения в точках</p>
          <button className="btn btn-primary">Открыть</button>
        </div>

        <div className="card" onClick={() => setShowIntegralModal(true)} style={{ cursor: 'pointer' }}>
          <h2>∫ Вычисление интеграла</h2>
          <p>Параллельное вычисление определённого интеграла</p>
          <button className="btn btn-primary">Открыть</button>
        </div>

        <div className="card" onClick={() => setShowCompositeModal(true)} style={{ cursor: 'pointer' }}>
          <h2>🔗 Составные функции</h2>
          <p>Создайте композицию из простых функций</p>
          <button className="btn btn-primary">Открыть</button>
        </div>
      </div>

      <FunctionsList userId={user?.userId} refreshKey={refreshKey} />

      {showArraysModal && (
        <CreateFromArraysModal
          onClose={() => setShowArraysModal(false)}
          onSuccess={handleFunctionCreated}
        />
      )}

      {showMathModal && (
        <CreateFromMathModal
          onClose={() => setShowMathModal(false)}
          onSuccess={handleFunctionCreated}
        />
      )}

      {showSettingsModal && (
        <SettingsModal
          onClose={() => setShowSettingsModal(false)}
        />
      )}

      {showOperationsModal && (
        <FunctionOperationsModal
          onClose={() => setShowOperationsModal(false)}
          onSuccess={handleFunctionCreated}
        />
      )}

      {showDifferentiateModal && (
        <DifferentiateModal
          onClose={() => setShowDifferentiateModal(false)}
          onSuccess={handleFunctionCreated}
        />
      )}

      {showExplorer && (
        <FunctionExplorer
          onClose={() => setShowExplorer(false)}
          onSuccess={handleFunctionCreated}
        />
      )}

      {showIntegralModal && (
        <IntegralModal
          onClose={() => setShowIntegralModal(false)}
        />
      )}

      {showCompositeModal && (
        <CompositeModal
          onClose={() => setShowCompositeModal(false)}
          onSuccess={handleFunctionCreated}
        />
      )}
    </div>
  );
}

export default Dashboard;

