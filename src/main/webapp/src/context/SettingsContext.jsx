import React, { createContext, useContext, useState, useEffect } from 'react';

const SettingsContext = createContext();

export const useSettings = () => {
  const context = useContext(SettingsContext);
  if (!context) {
    throw new Error('useSettings must be used within SettingsProvider');
  }
  return context;
};

export const SettingsProvider = ({ children }) => {
  const [factoryType, setFactoryType] = useState(() => {
    return localStorage.getItem('factoryType') || 'ARRAY';
  });

  const [theme, setTheme] = useState(() => {
    return localStorage.getItem('theme') || 'light';
  });

  useEffect(() => {
    localStorage.setItem('factoryType', factoryType);
  }, [factoryType]);

  useEffect(() => {
    localStorage.setItem('theme', theme);
    // Применяем тему к body
    document.body.setAttribute('data-theme', theme);
  }, [theme]);

  const value = {
    factoryType,
    setFactoryType,
    theme,
    setTheme,
  };

  return (
    <SettingsContext.Provider value={value}>
      {children}
    </SettingsContext.Provider>
  );
};

