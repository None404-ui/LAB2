package API.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppConfig {

    // === НАСТРОЙКИ СЕРВЕРА ===
    public static final int SERVER_PORT = 8080;
    public static final String API_BASE_PATH = "/api/v1";

    // === НАСТРОЙКИ БАЗЫ ДАННЫХ ===
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/labs_db";
    public static final String DB_USERNAME = "lab_user";
    public static final String DB_PASSWORD = "12345678";

    // === НАСТРОЙКИ ПАГИНАЦИИ ===
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // === CORS НАСТРОЙКИ ===
    public static final String ALLOWED_ORIGINS = "*";
    public static final String ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String ALLOWED_HEADERS = "Content-Type,Authorization";

    // === НАСТРОЙКИ БЕЗОПАСНОСТИ ===
    public static final boolean AUTH_ENABLED = false;
    public static final String JWT_SECRET = "lab-secret-key";

    // === СООБЩЕНИЯ ОБ ОШИБКАХ ===
    public static String getNotFoundMessage(String entity) {
        return entity + " not found";
    }

    public static String getValidationErrorMessage() {
        return "Validation failed";
    }

    // === JSON КОНФИГУРАЦИЯ ===
    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // УБРАТЬ JavaTimeModule - будем использовать String для дат
        return mapper;
    }

    // === ЛОГИРОВАНИЕ ===
    public static void logServerStart() {
        System.out.println("=== MATH FUNCTIONS API ===");
        System.out.println("Server started on port: " + SERVER_PORT);
        System.out.println("API Base: " + API_BASE_PATH);
        System.out.println("Database: " + DB_URL);
        System.out.println("==========================");
    }
}