CREATE TABLE IF NOT EXISTS computed_points (
    point_id SERIAL PRIMARY KEY,
    function_id INTEGER NOT NULL,
    x_value DOUBLE PRECISION NOT NULL,
    y_value DOUBLE PRECISION NOT NULL,

    FOREIGN KEY (function_id) REFERENCES functions(function_id) ON DELETE CASCADE,
    UNIQUE(function_id, x_value)
);