package api.controller;

import api.dto.ApiResponse;
import api.dto.CreateUserRequest;
import api.dto.UserDto;
import api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        logger.info("Получен запрос на получение списка всех пользователей");
        try {
            List<UserDto> users = userService.getAllUsers();
            logger.info("Успешно получен список из {} пользователей", users.size());
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody CreateUserRequest request) {
        logger.info("Получен запрос на создание пользователя: username={}, email={}",
                   request.getUsername(), request.getEmail());
        try {
            UserDto user = userService.createUser(request);
            logger.info("Успешно создан пользователь с ID: {}", user.getUserId());
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя {}: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
