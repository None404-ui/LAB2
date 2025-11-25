import axios from 'axios';

const API_BASE_URL = '/api/v1';

const client = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Добавляем Basic Auth к каждому запросу если пользователь залогинен
client.interceptors.request.use((config) => {
  const auth = localStorage.getItem('auth');
  if (auth) {
    config.headers.Authorization = `Basic ${auth}`;
  }
  // Логируем запрос для отладки
  console.log('API Request:', config.method?.toUpperCase(), config.url, config.data);
  return config;
});

// Обработка ошибок
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth');
      localStorage.removeItem('user');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  register: (username, email, password) =>
    client.post('/auth/register', { username, email, password }),
  
  login: async (username, password) => {
    const auth = btoa(`${username}:${password}`);
    // Сохраняем auth для последующих запросов
    localStorage.setItem('auth', auth);
    
    // Получаем информацию о текущем пользователе
    const response = await client.get('/users/me');
    
    if (response.data.success) {
      const user = response.data.data;
      localStorage.setItem('user', JSON.stringify(user));
      return { auth, username, userId: user.userId, user };
    }
    
    throw new Error('Failed to get user info');
  },

  getCurrentUser: () => client.get('/users/me'),
};

export const functionsAPI = {
  getUserFunctions: (userId) => client.get(`/functions/user/${userId}`),
  
  getFunctionById: (functionId) => client.get(`/functions/by-id/${functionId}`),
  
  createFromArrays: (name, xValues, yValues, factoryType = 'ARRAY') =>
    client.post('/functions/create-from-arrays', {
      name,
      xValues,
      yValues,
      factoryType,
    }),
  
  createFromMath: (name, mathFunctionType, xFrom, xTo, count, factoryType = 'ARRAY') =>
    client.post('/functions/create-from-math', {
      name,
      mathFunctionType,
      xFrom,
      xTo,
      count,
      factoryType,
    }),
  
  getMathFunctions: () => client.get('/functions/available-math-functions'),
  
  performOperation: (functionId1, functionId2, operation, resultName, factoryType = 'ARRAY') =>
    client.post('/functions/operate', {
      functionId1,
      functionId2,
      operation,
      resultName,
      factoryType,
    }),
  
  differentiate: (functionId, resultName, factoryType = 'ARRAY') =>
    client.post('/functions/differentiate', {
      functionId,
      resultName,
      factoryType,
    }),
  
  updateYValues: (functionId, yValues) =>
    client.put(`/functions/by-id/${functionId}/y-values`, {
      yValues,
    }),
  
  updateName: (functionId, name) =>
    client.put(`/functions/by-id/${functionId}/name`, {
      name,
    }),
  
  applyFunction: (functionId, x) =>
    client.post(`/functions/by-id/${functionId}/apply`, {
      x,
    }),
  
  insertPoint: (functionId, x, y) =>
    client.post(`/functions/by-id/${functionId}/insert-point`, {
      x,
      y,
    }),
  
  removePoint: (functionId, index) =>
    client.delete(`/functions/by-id/${functionId}/remove-point/${index}`),
  
  deleteFunction: (functionId) =>
    client.delete(`/functions/by-id/${functionId}`),
  
  calculateIntegral: (functionId, threadCount) =>
    client.post(`/functions/by-id/${functionId}/integrate`, { threadCount }),
  
  createCompositeFunction: (name, innerFunction, outerFunction) =>
    client.post('/functions/composite', { name, innerFunction, outerFunction }),
  
  getMaxThreads: () => client.get('/functions/max-threads'),
};

export default client;
