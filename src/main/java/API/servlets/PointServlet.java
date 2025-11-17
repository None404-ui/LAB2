package API.servlets;

import API.dtoForApi.*;
import API.service.MathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class PointServlet extends HttpServlet {
    private final MathService mathService = new MathService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/v1/points - список всех точек
                int page = getIntParameter(request, "page", 0);
                int size = getIntParameter(request, "size", 10);

                List<PointResponse> points = mathService.getPoints(page, size);
                ApiResponse<List<PointResponse>> apiResponse = ApiResponse.success(points);

                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                ApiResponse<String> errorResponse = ApiResponse.error("Point details endpoint not implemented");
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }

        } catch (Exception e) {
            handleException(response, e, "Error processing GET request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/v1/points - создание точки
                PointRequest pointRequest = objectMapper.readValue(request.getReader(), PointRequest.class);
                PointResponse point = mathService.createPoint(pointRequest);
                ApiResponse<PointResponse> apiResponse = ApiResponse.success(point);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ApiResponse<String> errorResponse = ApiResponse.error("Invalid POST endpoint");
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }

        } catch (Exception e) {
            handleException(response, e, "Error processing POST request");
        }
    }

    // Вспомогательные методы
    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private void handleException(HttpServletResponse response, Exception e, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ApiResponse<String> errorResponse = ApiResponse.error(message + ": " + e.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        e.printStackTrace();
    }
}