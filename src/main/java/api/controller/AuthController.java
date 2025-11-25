package api.controller;

import api.dto.ApiResponse;
import api.dto.RegisterRequest;
import api.dto.UserDto;
import api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        logger.info("Получен запрос на регистрацию пользователя: username={}, email={}",
                request.getUsername(), request.getEmail());
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Имя пользователя не может быть пустым");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email не может быть пустым");
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
            }

            UserDto user = userService.registerUser(request);
            logger.info("Пользователь {} успешно зарегистрирован с ID: {}", 
                    request.getUsername(), user.getUserId());
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", 
                    request.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}





