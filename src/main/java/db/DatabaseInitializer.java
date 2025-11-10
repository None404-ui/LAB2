package db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        System.out.println("Starting database initialization...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Выполняем скрипты по порядку
            System.out.println("1");
            executeScript(conn, "scripts/CreateUsers.sql");
            System.out.println("2");
            executeScript(conn, "scripts/CreateFunctions.sql");
            System.out.println("3");
            executeScript(conn, "scripts/CreateComputedPoints.sql");
            System.out.println("4");
            executeScript(conn, "scripts/CreateIndexes.sql");

            System.out.println("Database initialized successfully!");

        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeScript(Connection conn, String scriptPath) throws Exception {
        System.out.println("Executing script: " + scriptPath);

        InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(scriptPath);

        if (inputStream == null) {
            throw new RuntimeException("Script not found: " + scriptPath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Пропускаем комментарии и пустые строки
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }

                sqlBuilder.append(line);

                // Если строка заканчивается на;, выполняем SQL
                if (trimmedLine.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    try {
                        stmt.execute(sql);
                        System.out.println("Executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("Error executing SQL: " + sql);
                        throw e;
                    }
                    sqlBuilder.setLength(0); // Очищаем builder
                }
            }
        }
    }

    // Метод для запуска из main
    public static void main(String[] args) {
        initializeDatabase();
    }


}