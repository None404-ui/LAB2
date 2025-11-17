package api.service;

import api.dto.ComputedPointDto;
import api.dto.CreateComputedPointRequest;
import api.security.CurrentUserService;
import entities.ComputedPoint;
import entities.Function;
import entities.RoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ComputedPointService.class);

    @Autowired
    private ComputedPointRepository computedPointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public List<ComputedPointDto> getAllComputedPoints() {
        currentUserService.requireRole(RoleName.ROLE_ADMIN);
        logger.info("Admin {} requested all computed points", currentUserService.getCurrentUsername());
        return computedPointRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ComputedPointDto> getComputedPointsByFunctionId(Integer functionId) {
        Function function = findFunction(functionId);
        currentUserService.requireFunctionOwnerOrAdmin(function);
        logger.info("User {} fetching computed points for function {}", currentUserService.getCurrentUsername(), functionId);
        return computedPointRepository.findByFunction(function).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ComputedPointDto createComputedPoint(CreateComputedPointRequest request) {
        Function function = findFunction(request.getFunctionId());
        currentUserService.requireFunctionOwnerOrAdmin(function);

        Optional<ComputedPoint> existingPoint = computedPointRepository.findByFunctionAndXValue(
                function, request.getXValue());
        if (existingPoint.isPresent()) {
            throw new IllegalArgumentException("Computed point with this x value already exists for the function");
        }

        ComputedPoint computedPoint = new ComputedPoint();
        computedPoint.setFunction(function);
        computedPoint.setXValue(request.getXValue());
        computedPoint.setYValue(request.getYValue());

        ComputedPoint savedPoint = computedPointRepository.save(computedPoint);
        logger.info("Computed point saved for function {} at x={}", request.getFunctionId(), request.getXValue());
        return convertToDto(savedPoint);
    }

    private Function findFunction(Integer functionId) {
        return functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found"));
    }

    private ComputedPointDto convertToDto(ComputedPoint computedPoint) {
        return new ComputedPointDto(
                computedPoint.getFunction().getFunctionId(),
                computedPoint.getXValue(),
                computedPoint.getYValue(),
                LocalDateTime.now()
        );
    }
}
