package api.controller;

import api.dto.ApiResponse;
import api.dto.CreateFunctionRequest;
import api.dto.FunctionDto;
import api.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/functions")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FunctionDto>>> getAllFunctions() {
        logger.info("Получен запрос на получение списка всех функций");
        try {
            List<FunctionDto> functions = functionService.getAllFunctions();
            logger.info("Успешно получен список из {} функций", functions.size());
            return ResponseEntity.ok(ApiResponse.success(functions));
        } catch (Exception e) {
            logger.error("Ошибка при получении списка функций: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FunctionDto>> createFunction(@RequestBody CreateFunctionRequest request) {
        logger.info("Получен запрос на создание функции: name={}, userId={}",
                   request.getName(), request.getUserId());
        try {
            FunctionDto function = functionService.createFunction(request);
            logger.info("Успешно создана функция с ID: {} для пользователя {}",
                       function.getFunctionId(), function.getUserId());
            return ResponseEntity.ok(ApiResponse.success(function));
        } catch (Exception e) {
            logger.error("Ошибка при создании функции {} для пользователя {}: {}",
                        request.getName(), request.getUserId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FunctionDto>>> getUserFunctions(@PathVariable Integer userId) {
        logger.info("Получен запрос на получение функций пользователя с ID: {}", userId);
        try {
            List<FunctionDto> functions = functionService.getFunctionsByUserId(userId);
            logger.info("Успешно получены функции пользователя {}: {} функций",
                       userId, functions.size());
            return ResponseEntity.ok(ApiResponse.success(functions));
        } catch (Exception e) {
            logger.error("Ошибка при получении функций пользователя {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
