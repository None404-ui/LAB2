package api.controller;

import api.dto.*;
import api.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/functions")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
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

    @PostMapping("/from-arrays")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> createFunctionFromArrays(@RequestBody CreateFunctionFromArraysRequest request) {
        logger.info("Получен запрос на создание функции из массивов: name={}", request.getName());
        try {
            FunctionDto function = functionService.createFunctionFromArrays(request);
            logger.info("Успешно создана функция из массивов с ID: {}", function.getFunctionId());
            return ResponseEntity.ok(ApiResponse.success(function));
        } catch (Exception e) {
            logger.error("Ошибка при создании функции из массивов: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/from-math")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> createFunctionFromMath(@RequestBody CreateFunctionFromMathRequest request) {
        logger.info("Получен запрос на создание функции из математической функции: name={}, type={}", 
                   request.getName(), request.getMathFunctionType());
        try {
            FunctionDto function = functionService.createFunctionFromMath(request);
            logger.info("Успешно создана функция из математической функции с ID: {}", function.getFunctionId());
            return ResponseEntity.ok(ApiResponse.success(function));
        } catch (Exception e) {
            logger.error("Ошибка при создании функции из математической функции: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/math-functions")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableMathFunctions() {
        logger.info("Получен запрос на получение списка доступных математических функций");
        try {
            List<String> mathFunctions = functionService.getAvailableMathFunctions();
            return ResponseEntity.ok(ApiResponse.success(mathFunctions));
        } catch (Exception e) {
            logger.error("Ошибка при получении списка математических функций: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{functionId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> getFunctionById(@PathVariable Integer functionId) {
        logger.info("Получен запрос на получение функции с ID: {}", functionId);
        try {
            FunctionDto function = functionService.getFunctionById(functionId);
            return ResponseEntity.ok(ApiResponse.success(function));
        } catch (Exception e) {
            logger.error("Ошибка при получении функции {}: {}", functionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/operate")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> performOperation(@RequestBody FunctionOperationRequest request) {
        logger.info("Получен запрос на выполнение операции {} над функциями {} и {}", 
                   request.getOperation(), request.getFunctionId1(), request.getFunctionId2());
        try {
            FunctionDto result = functionService.performOperation(
                    request.getFunctionId1(),
                    request.getFunctionId2(),
                    request.getOperation(),
                    request.getResultName(),
                    request.getFactoryType()
            );
            logger.info("Операция {} выполнена, результат с ID: {}", 
                       request.getOperation(), result.getFunctionId());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("Ошибка при выполнении операции: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/differentiate")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> differentiate(@RequestBody DifferentiateRequest request) {
        logger.info("Получен запрос на дифференцирование функции {}", request.getFunctionId());
        try {
            FunctionDto result = functionService.differentiate(
                    request.getFunctionId(),
                    request.getResultName(),
                    request.getFactoryType()
            );
            logger.info("Функция продифференцирована, результат с ID: {}", result.getFunctionId());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("Ошибка при дифференцировании: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{functionId}/y-values")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> updateYValues(
            @PathVariable Integer functionId,
            @RequestBody UpdateFunctionPointsRequest request) {
        logger.info("Получен запрос на обновление Y значений функции {}", functionId);
        try {
            FunctionDto result = functionService.updateYValues(functionId, request.getYValues());
            logger.info("Y значения функции {} обновлены", functionId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("Ошибка при обновлении Y значений: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{functionId}/apply")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<ApplyResponse>> applyFunction(
            @PathVariable Integer functionId,
            @RequestBody ApplyRequest request) {
        logger.info("Получен запрос на вычисление значения функции {} в точке x={}", functionId, request.getX());
        try {
            double y = functionService.applyFunction(functionId, request.getX());
            ApplyResponse response = new ApplyResponse(request.getX(), y);
            logger.info("Значение функции {} в точке x={} равно y={}", functionId, request.getX(), y);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("Ошибка при вычислении значения: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{functionId}/insert-point")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> insertPoint(
            @PathVariable Integer functionId,
            @RequestBody InsertPointRequest request) {
        logger.info("Получен запрос на вставку точки в функцию {}: x={}, y={}", functionId, request.getX(), request.getY());
        try {
            FunctionDto result = functionService.insertPoint(functionId, request);
            logger.info("Точка успешно вставлена в функцию {}", functionId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("Ошибка при вставке точки: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{functionId}/remove-point/{index}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<FunctionDto>> removePoint(
            @PathVariable Integer functionId,
            @PathVariable int index) {
        logger.info("Получен запрос на удаление точки {} из функции {}", index, functionId);
        try {
            FunctionDto result = functionService.removePoint(functionId, index);
            logger.info("Точка {} успешно удалена из функции {}", index, functionId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("Ошибка при удалении точки: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
