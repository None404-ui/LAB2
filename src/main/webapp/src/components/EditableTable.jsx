import React, { useState } from 'react';
import { functionsAPI } from '../api/client';
import './Modal.css';

function EditableTable({ func, editable, onUpdate, isInsertable, isRemovable }) {
  const [editedYValues, setEditedYValues] = useState(func?.yValues ? [...func.yValues] : []);
  const [hasChanges, setHasChanges] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [insertX, setInsertX] = useState('');
  const [insertY, setInsertY] = useState('');
  const [showInsertForm, setShowInsertForm] = useState(false);

  // Если func не загружен, показываем заглушку
  if (!func || !func.xValues || !func.yValues) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>Данные функции не загружены</div>;
  }

  const handleYChange = (index, value) => {
    const newYValues = [...editedYValues];
    newYValues[index] = parseFloat(value) || 0;
    setEditedYValues(newYValues);
    setHasChanges(true);
  };

  const saveChanges = async () => {
    setSaving(true);
    setError('');

    try {
      const response = await functionsAPI.updateYValues(func.functionId, editedYValues);
      if (response.data.success) {
        setHasChanges(false);
        if (onUpdate) {
          onUpdate(response.data.data);
        }
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка сохранения');
    } finally {
      setSaving(false);
    }
  };

  const cancelChanges = () => {
    setEditedYValues([...func.yValues]);
    setHasChanges(false);
    setError('');
  };

  const handleInsert = async () => {
    if (!insertX || !insertY) {
      setError('Введите значения X и Y для вставки');
      return;
    }

    setSaving(true);
    setError('');

    try {
      const response = await functionsAPI.insertPoint(
        func.functionId,
        parseFloat(insertX),
        parseFloat(insertY)
      );
      if (response.data.success) {
        setShowInsertForm(false);
        setInsertX('');
        setInsertY('');
        if (onUpdate) {
          onUpdate(response.data.data);
        }
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка вставки точки');
    } finally {
      setSaving(false);
    }
  };

  const handleRemove = async (index) => {
    if (!window.confirm(`Удалить точку #${index + 1}?`)) {
      return;
    }

    setSaving(true);
    setError('');

    try {
      const response = await functionsAPI.removePoint(func.functionId, index);
      if (response.data.success) {
        if (onUpdate) {
          onUpdate(response.data.data);
        }
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка удаления точки');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="editable-table-container">
      {error && <div className="error-message" style={{ marginBottom: '10px' }}>{error}</div>}
      
      <div className="points-table-container" style={{ maxHeight: '300px' }}>
        <table className="points-table">
          <thead>
            <tr>
              <th>#</th>
              <th>X</th>
              <th>Y</th>
              {editable && isRemovable && <th>Действия</th>}
            </tr>
          </thead>
          <tbody>
            {func.xValues.map((x, index) => (
              <tr key={index}>
                <td>{index + 1}</td>
                <td>{x.toFixed(6)}</td>
                <td>
                  {editable ? (
                    <input
                      type="number"
                      step="any"
                      value={editedYValues[index]}
                      onChange={(e) => handleYChange(index, e.target.value)}
                      disabled={saving}
                      style={{ width: '100%' }}
                    />
                  ) : (
                    func.yValues[index].toFixed(6)
                  )}
                </td>
                {editable && isRemovable && (
                  <td>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleRemove(index)}
                      disabled={saving}
                      title="Удалить точку"
                    >
                      ✕
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {editable && hasChanges && (
        <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
          <button
            className="btn btn-primary"
            onClick={saveChanges}
            disabled={saving}
            style={{ flex: 1 }}
          >
            {saving ? 'Сохранение...' : 'Сохранить изменения'}
          </button>
          <button
            className="btn btn-secondary"
            onClick={cancelChanges}
            disabled={saving}
            style={{ flex: 1 }}
          >
            Отменить
          </button>
        </div>
      )}

      {editable && isInsertable && (
        <div style={{ marginTop: '15px', padding: '10px', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
          {!showInsertForm ? (
            <button
              className="btn btn-success"
              onClick={() => setShowInsertForm(true)}
              disabled={saving}
            >
              + Вставить точку
            </button>
          ) : (
            <div>
              <h4 style={{ marginTop: 0, marginBottom: '10px' }}>Вставка новой точки</h4>
              <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', fontSize: '12px' }}>X:</label>
                  <input
                    type="number"
                    step="any"
                    value={insertX}
                    onChange={(e) => setInsertX(e.target.value)}
                    placeholder="X координата"
                    disabled={saving}
                    style={{ width: '100%' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', fontSize: '12px' }}>Y:</label>
                  <input
                    type="number"
                    step="any"
                    value={insertY}
                    onChange={(e) => setInsertY(e.target.value)}
                    placeholder="Y координата"
                    disabled={saving}
                    style={{ width: '100%' }}
                  />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button
                  className="btn btn-success btn-sm"
                  onClick={handleInsert}
                  disabled={saving}
                  style={{ flex: 1 }}
                >
                  {saving ? 'Вставка...' : 'Вставить'}
                </button>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => {
                    setShowInsertForm(false);
                    setInsertX('');
                    setInsertY('');
                    setError('');
                  }}
                  disabled={saving}
                  style={{ flex: 1 }}
                >
                  Отмена
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default EditableTable;

