package API;

import API.config.AppConfig;
import API.controller.MathFunctionsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {
    private static final ObjectMapper objectMapper = AppConfig.getObjectMapper();
    private static final MathFunctionsController controller = new MathFunctionsController();

    public static void main(String[] args) {
        AppConfig.logServerStart();

        try (ServerSocket serverSocket = new ServerSocket(AppConfig.SERVER_PORT)) {
            System.out.println("Server ready! Test with: curl http://localhost:" + AppConfig.SERVER_PORT + AppConfig.API_BASE_PATH + "/users");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Читаем запрос
            String requestLine = in.readLine();
            if (requestLine == null) return;

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            // Пропускаем headers
            String line;
            while (!(line = in.readLine()).isEmpty()) {}

            // Обрабатываем API
            String response = processApiCall(method, path, in);

            // Отправляем ответ
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println("Access-Control-Allow-Origin: " + AppConfig.ALLOWED_ORIGINS);
            out.println();
            out.println(response);

        } catch (Exception e) {
            System.err.println("Request error: " + e.getMessage());
            // В случае ошибки возвращаем JSON с ошибкой
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out.println("HTTP/1.1 500 Internal Server Error");
                out.println("Content-Type: application/json");
                out.println();
                out.println("{\"success\":false,\"error\":\"Internal server error\"}");
            } catch (IOException ioException) {
                System.err.println("Error sending error response: " + ioException.getMessage());
            }
        }
    }

    private static String processApiCall(String method, String path, BufferedReader in) {
        String base = AppConfig.API_BASE_PATH;

        try {
            // === USERS ===
            if (path.equals(base + "/users") && method.equals("GET")) {
                return controller.getUsers(0, AppConfig.DEFAULT_PAGE_SIZE);
            }
            if (path.equals(base + "/users") && method.equals("POST")) {
                String body = readRequestBody(in);
                return controller.createUser(body);
            }

            // === FUNCTIONS ===
            if (path.equals(base + "/functions") && method.equals("GET")) {
                return controller.getFunctions(0, AppConfig.DEFAULT_PAGE_SIZE);
            }
            if (path.equals(base + "/functions") && method.equals("POST")) {
                String body = readRequestBody(in);
                return controller.createFunction(body);
            }

            // === POINTS ===
            if (path.equals(base + "/points") && method.equals("GET")) {
                return controller.getPoints(0, AppConfig.DEFAULT_PAGE_SIZE);
            }
            if (path.equals(base + "/points") && method.equals("POST")) {
                String body = readRequestBody(in);
                return controller.createPoint(body);
            }

            // === USER FUNCTIONS ===
            if (path.matches(base + "/users/\\d+/functions") && method.equals("GET")) {
                Integer userId = extractId(path, base + "/users/", "/functions");
                return controller.getUserFunctions(userId);
            }

            // === FUNCTION POINTS ===
            if (path.matches(base + "/functions/\\d+/points") && method.equals("GET")) {
                Integer functionId = extractId(path, base + "/functions/", "/points");
                return controller.getFunctionPoints(functionId);
            }

            return "{\"success\":false,\"error\":\"Endpoint not found: " + path + "\"}";

        } catch (Exception e) {
            // Обрабатываем исключения из контроллера
            System.err.println("API call error: " + e.getMessage());
            return "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private static String readRequestBody(BufferedReader in) throws IOException {
        StringBuilder body = new StringBuilder();
        while (in.ready()) {
            body.append((char) in.read());
        }
        return body.toString();
    }

    private static Integer extractId(String path, String prefix, String suffix) {
        try {
            String idStr = path.substring(prefix.length(), path.length() - suffix.length());
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in path: " + path);
        }
    }
}