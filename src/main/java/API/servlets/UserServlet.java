package API.servlets;

import API.dtoForApi.*;
import API.service.MathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class UserServlet extends HttpServlet {
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
                // GET /api/v1/users - список пользователей
                int page = getIntParameter(request, "page", 0);
                int size = getIntParameter(request, "size", 10);

                List<UserResponse> users = mathService.getUsers(page, size);
                ApiResponse<List<UserResponse>> apiResponse = ApiResponse.success(users);

                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

            } else if (pathInfo.matches("/\\d+/functions")) {
                // GET /api/v1/users/{id}/functions - функции пользователя
                Integer userId = extractIdFromPath(pathInfo);
                List<FunctionResponse> functions = mathService.getUserFunctions(userId);
                ApiResponse<List<FunctionResponse>> apiResponse = ApiResponse.success(functions);

                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

            } else {
                // GET /api/v1/users/{id} - конкретный пользователь (заглушка)
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                ApiResponse<String> errorResponse = ApiResponse.error("User details endpoint not implemented");
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
                // POST /api/v1/users - создание пользователя
                UserRequest userRequest = objectMapper.readValue(request.getReader(), UserRequest.class);
                UserResponse user = mathService.createUser(userRequest);
                ApiResponse<UserResponse> apiResponse = ApiResponse.success(user);

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

    private Integer extractIdFromPath(String pathInfo) {
        try {
            // /123/functions -> 123
            String[] parts = pathInfo.split("/");
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID in path: " + pathInfo);
        }
    }

    private void handleException(HttpServletResponse response, Exception e, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ApiResponse<String> errorResponse = ApiResponse.error(message + ": " + e.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        e.printStackTrace();
    }
}