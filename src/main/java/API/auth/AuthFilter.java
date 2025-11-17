package API.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        logger.debug("AuthFilter processing request: {} {}", httpRequest.getMethod(), path);

        // Пропускаем публичные endpoints
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register")) {
            logger.debug("Skipping auth for public endpoint: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Проверяем авторизацию
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Missing or invalid Authorization header for: {}", path);
            sendError(httpResponse, "Authorization required");
            return;
        }

        // Декодируем Basic Auth
        String credentials = new String(Base64.getDecoder().decode(authHeader.substring(6)));
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {
            logger.warn("Invalid credentials format for: {}", path);
            sendError(httpResponse, "Invalid credentials");
            return;
        }

        String username = parts[0];
        String password = parts[1];

        // Аутентификация
        UserPrincipal user = AuthService.authenticate(username, password);
        if (user == null) {
            logger.warn("Authentication failed for user: {}", username);
            sendError(httpResponse, "Invalid username or password");
            return;
        }

        // Проверяем права доступа
        if (!hasAccess(user, httpRequest.getMethod(), path)) {
            logger.warn("Access denied for user: {} to {} {}", username, httpRequest.getMethod(), path);
            sendError(httpResponse, "Access denied");
            return;
        }

        logger.info("Access granted for user: {} to {} {}", username, httpRequest.getMethod(), path);
        request.setAttribute("user", user);
        chain.doFilter(request, response);
    }

    private boolean hasAccess(UserPrincipal user, String method, String path) {
        // ADMIN имеет полный доступ
        if (user.hasRole(Role.ADMIN)) {
            return true;
        }

        // GUEST может только читать
        if (user.hasRole(Role.GUEST) && !method.equals("GET")) {
            return false;
        }

        // USER не может управлять пользователями (кроме своих данных)
        if (user.hasRole(Role.USER) && path.startsWith("/api/v1/users/") && !path.endsWith("/" + user.getName())) {
            return false;
        }

        return true;
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
    }
}