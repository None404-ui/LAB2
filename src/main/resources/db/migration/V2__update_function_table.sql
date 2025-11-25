-- Миграция для изменения структуры таблицы functions
-- Добавляем новые колонки для хранения массивов x и y

-- Удаляем старую колонку expression если она есть
ALTER TABLE functions DROP COLUMN IF EXISTS expression;

-- Добавляем новые колонки
ALTER TABLE functions ADD COLUMN IF NOT EXISTS function_type VARCHAR(50);
ALTER TABLE functions ADD COLUMN IF NOT EXISTS x_values TEXT;
ALTER TABLE functions ADD COLUMN IF NOT EXISTS y_values TEXT;
ALTER TABLE functions ADD COLUMN IF NOT EXISTS count INTEGER;

-- Удаляем таблицу computed_points если она существует (больше не нужна)
DROP TABLE IF EXISTS computed_points CASCADE;





