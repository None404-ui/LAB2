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

public class FunctionServlet extends HttpServlet {
    private final MathService mathService = new MathService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(FunctionServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== GET {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/v1/functions - список функций
                int page = getIntParameter(request, "page", 0);
                int size = getIntParameter(request, "size", 10);

                logger.debug("Getting functions list - page: {}, size: {}", page, size);
                List<FunctionResponse> functions = mathService.getFunctions(page, size);

                ApiResponse<List<FunctionResponse>> apiResponse = ApiResponse.success(functions);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.getWriter().write(jsonResponse);
                logger.info("Get functions list completed successfully: {} functions retrieved", functions.size());

            } else if (pathInfo.matches("/\\d+/points")) {
                // GET /api/v1/functions/{id}/points - точки функции
                Integer functionId = extractIdFromPath(pathInfo);
                logger.debug("Getting points for function ID: {}", functionId);

                List<PointResponse> points = mathService.getFunctionPoints(functionId);
                ApiResponse<List<PointResponse>> apiResponse = ApiResponse.success(points);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.getWriter().write(jsonResponse);
                logger.info("Get function points completed: {} points for function {}", points.size(), functionId);

            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/v1/functions/{id} - получить функцию по ID
                Integer functionId = extractIdFromPath(pathInfo);
                logger.debug("Getting function by ID: {}", functionId);

                FunctionResponse function = mathService.getFunctionById(functionId);
                if (function != null) {
                    ApiResponse<FunctionResponse> apiResponse = ApiResponse.success(function);
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Get function by ID completed: function found - {}", function.getName());
                } else {
                    logger.warn("Function not found with ID: {}", functionId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Function not found with ID: " + functionId);
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
                // POST /api/v1/functions - создание функции
                String requestBody = readRequestBody(request);
                logger.debug("Creating function with request body: {}", requestBody);

                FunctionRequest functionRequest = objectMapper.readValue(requestBody, FunctionRequest.class);
                logger.debug("Parsed function request: name={}, expression={}, userId={}",
                        functionRequest.getName(), functionRequest.getExpression(), functionRequest.getUserId());

                FunctionResponse function = mathService.createFunction(functionRequest);
                ApiResponse<FunctionResponse> apiResponse = ApiResponse.success(function);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(jsonResponse);
                logger.info("Function created successfully: ID={}, name={}",
                        function.getFunctionId(), function.getName());

            } else {
                logger.warn("Invalid POST endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid POST endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing POST request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error creating function: " + e.getMessage());
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
                // PUT /api/v1/functions/{id} - обновление функции
                Integer functionId = extractIdFromPath(pathInfo);
                String requestBody = readRequestBody(request);
                logger.debug("Updating function ID {} with data: {}", functionId, requestBody);

                FunctionRequest functionRequest = objectMapper.readValue(requestBody, FunctionRequest.class);
                boolean updated = mathService.updateFunction(functionId, functionRequest);

                if (updated) {
                    ApiResponse<String> apiResponse = ApiResponse.success("Function updated successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Function updated successfully: ID={}", functionId);
                } else {
                    logger.warn("Function not found for update: ID={}", functionId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Function not found with ID: " + functionId);
                }

            } else {
                logger.warn("Invalid PUT endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid PUT endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing PUT request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error updating function: " + e.getMessage());
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
                // DELETE /api/v1/functions/{id} - удаление функции
                Integer functionId = extractIdFromPath(pathInfo);
                logger.debug("Deleting function ID: {}", functionId);

                boolean deleted = mathService.deleteFunction(functionId);

                if (deleted) {
                    ApiResponse<String> apiResponse = ApiResponse.success("Function deleted successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Function deleted successfully: ID={}", functionId);
                } else {
                    logger.warn("Function not found for deletion: ID={}", functionId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Function not found with ID: " + functionId);
                }

            } else {
                logger.warn("Invalid DELETE endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid DELETE endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing DELETE request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error deleting function: " + e.getMessage());
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