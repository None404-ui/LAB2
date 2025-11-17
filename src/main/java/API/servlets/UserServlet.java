package API.servlets;

import API.dtoForApi.*;
import API.service.MathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {
    private final MathService mathService = new MathService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== GET {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/v1/users - список пользователей
                int page = getIntParameter(request, "page", 0);
                int size = getIntParameter(request, "size", 10);

                logger.debug("Getting users list - page: {}, size: {}", page, size);
                List<UserResponse> users = mathService.getUsers(page, size);

                ApiResponse<List<UserResponse>> apiResponse = ApiResponse.success(users);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.getWriter().write(jsonResponse);
                logger.info("Get users list completed successfully: {} users retrieved", users.size());

            } else if (pathInfo.matches("/\\d+/functions")) {
                // GET /api/v1/users/{id}/functions - функции пользователя
                Integer userId = extractIdFromPath(pathInfo);
                logger.debug("Getting functions for user ID: {}", userId);

                List<FunctionResponse> functions = mathService.getUserFunctions(userId);
                ApiResponse<List<FunctionResponse>> apiResponse = ApiResponse.success(functions);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.getWriter().write(jsonResponse);
                logger.info("Get user functions completed: {} functions for user {}", functions.size(), userId);

            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/v1/users/{id} - получить пользователя по ID
                Integer userId = extractIdFromPath(pathInfo);
                logger.debug("Getting user by ID: {}", userId);

                UserResponse user = mathService.getUserById(userId);
                if (user != null) {
                    ApiResponse<UserResponse> apiResponse = ApiResponse.success(user);
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Get user by ID completed: user found - {}", user.getUsername());
                } else {
                    logger.warn("User not found with ID: {}", userId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "User not found with ID: " + userId);
                }

            } else {
                logger.warn("Unknown GET endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(response, "Endpoint not found: " + pathInfo);
            }

        } catch (Exception e) {
            logger.error("Error processing GET request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Internal server error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== POST {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/v1/users - создание пользователя
                String requestBody = readRequestBody(request);
                logger.debug("Creating user with request body: {}", requestBody);

                UserRequest userRequest = objectMapper.readValue(requestBody, UserRequest.class);
                logger.debug("Parsed user request: username={}, email={}",
                        userRequest.getUsername(), userRequest.getEmail());

                UserResponse user = mathService.createUser(userRequest);
                ApiResponse<UserResponse> apiResponse = ApiResponse.success(user);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(jsonResponse);
                logger.info("User created successfully: ID={}, username={}",
                        user.getUserId(), user.getUsername());

            } else {
                logger.warn("Invalid POST endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid POST endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing POST request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error creating user: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== PUT {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // PUT /api/v1/users/{id} - обновление пользователя
                Integer userId = extractIdFromPath(pathInfo);
                String requestBody = readRequestBody(request);
                logger.debug("Updating user ID {} with data: {}", userId, requestBody);

                UserRequest userRequest = objectMapper.readValue(requestBody, UserRequest.class);
                boolean updated = mathService.updateUser(userId, userRequest);

                if (updated) {
                    ApiResponse<String> apiResponse = ApiResponse.success("User updated successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("User updated successfully: ID={}", userId);
                } else {
                    logger.warn("User not found for update: ID={}", userId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "User not found with ID: " + userId);
                }

            } else {
                logger.warn("Invalid PUT endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid PUT endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing PUT request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error updating user: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== DELETE {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // DELETE /api/v1/users/{id} - удаление пользователя
                Integer userId = extractIdFromPath(pathInfo);
                logger.debug("Deleting user ID: {}", userId);

                boolean deleted = mathService.deleteUser(userId);

                if (deleted) {
                    ApiResponse<String> apiResponse = ApiResponse.success("User deleted successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("User deleted successfully: ID={}", userId);
                } else {
                    logger.warn("User not found for deletion: ID={}", userId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "User not found with ID: " + userId);
                }

            } else {
                logger.warn("Invalid DELETE endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid DELETE endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing DELETE request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error deleting user: " + e.getMessage());
        }
    }

    // Вспомогательные методы
    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.trim().isEmpty()) {
            try {
                int value = Integer.parseInt(paramValue);
                logger.debug("Parameter {} = {}", paramName, value);
                return value;
            } catch (NumberFormatException e) {
                logger.warn("Invalid {} parameter: {}, using default: {}", paramName, paramValue, defaultValue);
                return defaultValue;
            }
        }
        logger.debug("Parameter {} not provided, using default: {}", paramName, defaultValue);
        return defaultValue;
    }

    private Integer extractIdFromPath(String pathInfo) {
        try {
            String[] parts = pathInfo.split("/");
            Integer id = Integer.parseInt(parts[1]);
            logger.debug("Extracted ID from path: {}", id);
            return id;
        } catch (NumberFormatException e) {
            logger.error("Invalid ID in path: {}", pathInfo);
            throw new IllegalArgumentException("Invalid ID in path: " + pathInfo);
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();
        logger.debug("Request body: {}", body);
        return body;
    }

    private void sendError(HttpServletResponse response, String error) throws IOException {
        logger.debug("Sending error response: {}", error);
        ApiResponse<String> errorResponse = ApiResponse.error(error);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}