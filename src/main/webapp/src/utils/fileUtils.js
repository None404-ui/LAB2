// Утилиты для работы с файлами функций

/**
 * Сохранить функцию в JSON файл (скачать)
 */
export const saveFunctionToFile = (func) => {
  const data = {
    name: func.name,
    functionType: func.functionType,
    xValues: func.xValues,
    yValues: func.yValues,
    count: func.count,
    exportedAt: new Date().toISOString(),
  };

  const json = JSON.stringify(data, null, 2);
  const blob = new Blob([json], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  
  const link = document.createElement('a');
  link.href = url;
  link.download = `${func.name.replace(/[^a-z0-9]/gi, '_')}_${Date.now()}.json`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};

/**
 * Загрузить функцию из JSON файла
 */
export const loadFunctionFromFile = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const data = JSON.parse(e.target.result);
        
        // Валидация
        if (!data.name || !data.xValues || !data.yValues) {
          throw new Error('Некорректный формат файла');
        }
        
        if (data.xValues.length !== data.yValues.length) {
          throw new Error('Массивы X и Y разной длины');
        }
        
        resolve(data);
      } catch (error) {
        reject(new Error(`Ошибка чтения файла: ${error.message}`));
      }
    };
    
    reader.onerror = () => {
      reject(new Error('Ошибка чтения файла'));
    };
    
    reader.readAsText(file);
  });
};

/**
 * Создать функцию из загруженных данных
 */
export const createFunctionFromLoadedData = async (data, factoryType, functionsAPI) => {
  try {
    // Убедимся что массивы - это массивы чисел
    const xValues = Array.isArray(data.xValues) ? data.xValues.map(Number) : [];
    const yValues = Array.isArray(data.yValues) ? data.yValues.map(Number) : [];
    
    if (xValues.length === 0 || yValues.length === 0) {
      throw new Error('Массивы X и Y не могут быть пустыми');
    }
    
    const response = await functionsAPI.createFromArrays(
      data.name,
      xValues,
      yValues,
      factoryType || data.factoryType || 'ARRAY'
    );
    
    return response.data;
  } catch (error) {
    const message = error.response?.data?.error || error.response?.data?.message || error.message;
    throw new Error(`Ошибка создания функции: ${message}`);
  }
};



