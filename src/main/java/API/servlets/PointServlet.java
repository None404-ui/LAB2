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

public class PointServlet extends HttpServlet {
    private final MathService mathService = new MathService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(PointServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo();
        logger.info("=== GET {} ===", pathInfo != null ? pathInfo : "/");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/v1/points - список точек
                int page = getIntParameter(request, "page", 0);
                int size = getIntParameter(request, "size", 10);

                logger.debug("Getting points list - page: {}, size: {}", page, size);
                List<PointResponse> points = mathService.getPoints(page, size);

                ApiResponse<List<PointResponse>> apiResponse = ApiResponse.success(points);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.getWriter().write(jsonResponse);
                logger.info("Get points list completed successfully: {} points retrieved", points.size());

            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/v1/points/{id} - получить точку по ID
                Integer pointId = extractIdFromPath(pathInfo);
                logger.debug("Getting point by ID: {}", pointId);

                PointResponse point = mathService.getPointById(pointId);
                if (point != null) {
                    ApiResponse<PointResponse> apiResponse = ApiResponse.success(point);
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Get point by ID completed: point found - ID={}", point.getPointId());
                } else {
                    logger.warn("Point not found with ID: {}", pointId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Point not found with ID: " + pointId);
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
                // POST /api/v1/points - создание точки
                String requestBody = readRequestBody(request);
                logger.debug("Creating point with request body: {}", requestBody);

                PointRequest pointRequest = objectMapper.readValue(requestBody, PointRequest.class);
                logger.debug("Parsed point request: functionId={}, xValue={}, yValue={}",
                        pointRequest.getFunctionId(), pointRequest.getXValue(), pointRequest.getYValue());

                PointResponse point = mathService.createPoint(pointRequest);
                ApiResponse<PointResponse> apiResponse = ApiResponse.success(point);
                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(jsonResponse);
                logger.info("Point created successfully: ID={}, functionId={}, x={}, y={}",
                        point.getPointId(), point.getFunctionId(), point.getXValue(), point.getYValue());

            } else {
                logger.warn("Invalid POST endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid POST endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing POST request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error creating point: " + e.getMessage());
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
                // PUT /api/v1/points/{id} - обновление точки
                Integer pointId = extractIdFromPath(pathInfo);
                String requestBody = readRequestBody(request);
                logger.debug("Updating point ID {} with data: {}", pointId, requestBody);

                PointRequest pointRequest = objectMapper.readValue(requestBody, PointRequest.class);
                boolean updated = mathService.updatePoint(pointId, pointRequest);

                if (updated) {
                    ApiResponse<String> apiResponse = ApiResponse.success("Point updated successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Point updated successfully: ID={}", pointId);
                } else {
                    logger.warn("Point not found for update: ID={}", pointId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Point not found with ID: " + pointId);
                }

            } else {
                logger.warn("Invalid PUT endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid PUT endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing PUT request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error updating point: " + e.getMessage());
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
                // DELETE /api/v1/points/{id} - удаление точки
                Integer pointId = extractIdFromPath(pathInfo);
                logger.debug("Deleting point ID: {}", pointId);

                boolean deleted = mathService.deletePoint(pointId);

                if (deleted) {
                    ApiResponse<String> apiResponse = ApiResponse.success("Point deleted successfully");
                    String jsonResponse = objectMapper.writeValueAsString(apiResponse);
                    response.getWriter().write(jsonResponse);
                    logger.info("Point deleted successfully: ID={}", pointId);
                } else {
                    logger.warn("Point not found for deletion: ID={}", pointId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(response, "Point not found with ID: " + pointId);
                }

            } else {
                logger.warn("Invalid DELETE endpoint: {}", pathInfo);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(response, "Invalid DELETE endpoint");
            }

        } catch (Exception e) {
            logger.error("Error processing DELETE request for path {}: {}", pathInfo, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "Error deleting point: " + e.getMessage());
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