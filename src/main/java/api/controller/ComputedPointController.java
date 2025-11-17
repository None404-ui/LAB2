package api.controller;

import api.dto.ApiResponse;
import api.dto.ComputedPointDto;
import api.dto.CreateComputedPointRequest;
import api.service.ComputedPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/points")
public class ComputedPointController {

    private static final Logger logger = LoggerFactory.getLogger(ComputedPointController.class);

    @Autowired
    private ComputedPointService computedPointService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ComputedPointDto>>> getAllPoints() {
        logger.info("Получен запрос на получение списка всех вычисленных точек");
        try {
            List<ComputedPointDto> points = computedPointService.getAllComputedPoints();
            logger.info("Успешно получен список из {} вычисленных точек", points.size());
            return ResponseEntity.ok(ApiResponse.success(points));
        } catch (Exception e) {
            logger.error("Ошибка при получении списка вычисленных точек: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<ComputedPointDto>> createPoint(@RequestBody CreateComputedPointRequest request) {
        logger.info("Получен запрос на создание вычисленной точки: functionId={}, x={}, y={}",
                   request.getFunctionId(), request.getXValue(), request.getYValue());
        try {
            ComputedPointDto point = computedPointService.createComputedPoint(request);
            logger.info("Успешно создана вычисленная точка для функции {} с координатами ({}, {})",
                       point.getFunctionId(), point.getXValue(), point.getYValue());
            return ResponseEntity.ok(ApiResponse.success(point));
        } catch (Exception e) {
            logger.error("Ошибка при создании вычисленной точки для функции {}: {}",
                        request.getFunctionId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/function/{functionId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<List<ComputedPointDto>>> getFunctionPoints(@PathVariable Integer functionId) {
        logger.info("Получен запрос на получение вычисленных точек функции с ID: {}", functionId);
        try {
            List<ComputedPointDto> points = computedPointService.getComputedPointsByFunctionId(functionId);
            logger.info("Успешно получены вычисленные точки функции {}: {} точек",
                       functionId, points.size());
            return ResponseEntity.ok(ApiResponse.success(points));
        } catch (Exception e) {
            logger.error("Ошибка при получении вычисленных точек функции {}: {}", functionId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
