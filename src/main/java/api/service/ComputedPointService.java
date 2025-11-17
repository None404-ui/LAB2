package api.service;

import api.dto.ComputedPointDto;
import api.dto.CreateComputedPointRequest;
import entities.ComputedPoint;
import entities.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.ComputedPointRepository;
import repositories.FunctionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComputedPointService {

    @Autowired
    private ComputedPointRepository computedPointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    public List<ComputedPointDto> getAllComputedPoints() {
        return computedPointRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ComputedPointDto> getComputedPointsByFunctionId(Integer functionId) {
        Optional<Function> function = functionRepository.findById(functionId);
        if (function.isEmpty()) {
            throw new IllegalArgumentException("Function not found");
        }
        return computedPointRepository.findByFunction(function.get()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ComputedPointDto createComputedPoint(CreateComputedPointRequest request) {
        // Проверяем, существует ли функция
        Optional<Function> function = functionRepository.findById(request.getFunctionId());
        if (function.isEmpty()) {
            throw new IllegalArgumentException("Function not found");
        }

        // Проверяем, не существует ли уже точка с таким x для этой функции
        Optional<ComputedPoint> existingPoint = computedPointRepository.findByFunctionAndXValue(
                function.get(), request.getXValue());
        if (existingPoint.isPresent()) {
            throw new IllegalArgumentException("Computed point with this x value already exists for the function");
        }

        ComputedPoint computedPoint = new ComputedPoint();
        computedPoint.setFunction(function.get());
        computedPoint.setXValue(request.getXValue());
        computedPoint.setYValue(request.getYValue());

        ComputedPoint savedPoint = computedPointRepository.save(computedPoint);
        return convertToDto(savedPoint);
    }

    private ComputedPointDto convertToDto(ComputedPoint computedPoint) {
        return new ComputedPointDto(
                computedPoint.getFunction().getFunctionId(),
                computedPoint.getXValue(),
                computedPoint.getYValue(),
                LocalDateTime.now() // В реальном приложении нужно брать из БД
        );
    }
}
