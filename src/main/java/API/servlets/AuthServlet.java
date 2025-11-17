package API.servlets;

import API.dtoForApi.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> users = new HashMap<>();


    static {
        users.put("admin", "ADMIN");
        users.put("user1", "USER");
        users.put("guest", "GUEST");
        logger.info("AuthServlet initialized with default users: {}", users.keySet());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getPathInfo();
        logger.info("=== AUTH {} ===", path != null ? path : "/");

        try {
            if (path == null || path.equals("/")) {
                handleLogin(request, response);
            } else if (path.equals("/register")) {
                handleRegister(request, response);
            } else if (path.equals("/login")) {
                handleLogin(request, response);
            } else {
                sendError(response, "Endpoint not found: " + path);
            }
        } catch (Exception e) {
            logger.error("Auth error: {}", e.getMessage(), e);
            sendError(response, "Authentication error: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = readRequestBody(request);
        logger.debug("Login request: {}", body);

        Map<String, String> credentials = objectMapper.readValue(body, HashMap.class);
        String username = credentials.get("username");
        String password = credentials.get("password");

        logger.debug("Login attempt - username: {}, password: {}", username, password);
        logger.debug("Available users: {}", users.keySet()); // Логируем доступных пользователей

        if (username == null || password == null) {
            sendError(response, "Username and password required");
            return;
        }

        // Простая аутентификация - пароль всегда "123"
        if (users.containsKey(username) && "123".equals(password)) {
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("role", users.get(username));
            result.put("message", "Login successful");
            result.put("availableUsers", users.keySet()); // Для отладки

            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(result);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            logger.info("User logged in: {} with role: {}", username, users.get(username));
        } else {
            logger.warn("Login failed for user: {}. Available users: {}", username, users.keySet());
            sendError(response, "Invalid username or password. Available users: " + users.keySet());
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = readRequestBody(request);
        logger.debug("Register request: {}", body);

        Map<String, String> data = objectMapper.readValue(body, HashMap.class);
        String username = data.get("username");
        String password = data.get("password");
        String role = data.get("role");

        logger.debug("Register attempt - username: {}, role: {}", username, role);

        if (username == null || password == null) {
            sendError(response, "Username and password required");
            return;
        }

        if (users.containsKey(username)) {
            logger.warn("User already exists: {}", username);
            sendError(response, "User already exists. Available users: " + users.keySet());
            return;
        }

        // Сохраняем пользователя в статическую map
        users.put(username, role != null ? role : "USER");

        Map<String, String> result = new HashMap<>();
        result.put("message", "User registered successfully");
        result.put("username", username);
        result.put("role", users.get(username));
        result.put("availableUsers", String.valueOf(users.keySet()));

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(result);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        logger.info("User registered: {} with role: {}. Total users: {}", username, users.get(username), users.size());
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void sendError(HttpServletResponse response, String error) throws IOException {
        logger.debug("Sending error: {}", error);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> errorResponse = ApiResponse.error(error);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}