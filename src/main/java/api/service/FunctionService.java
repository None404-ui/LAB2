package api.service;

import api.dto.CreateFunctionRequest;
import api.dto.FunctionDto;
import api.security.CurrentUserService;
import entities.Function;
import entities.RoleName;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.FunctionRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FunctionService {

    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    public List<FunctionDto> getAllFunctions() {
        currentUserService.requireRole(RoleName.ROLE_ADMIN);
        logger.info("Admin {} requested full function list", currentUserService.getCurrentUsername());
        return functionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FunctionDto> getFunctionsByUserId(Integer userId) {
        currentUserService.requireSelfOrAdmin(userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        logger.info("User {} fetching functions for userId={}", currentUserService.getCurrentUsername(), userId);
        return functionRepository.findByUser(user.get()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FunctionDto createFunction(CreateFunctionRequest request) {
        Integer targetUserId = request.getUserId() != null
                ? request.getUserId()
                : currentUserService.getCurrentUserId();
        currentUserService.requireSelfOrAdmin(targetUserId);

        Optional<User> user = userRepository.findById(targetUserId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Function function = new Function();
        function.setName(request.getName());
        function.setExpression(request.getExpression());
        function.setUser(user.get());

        Function savedFunction = functionRepository.save(function);
        logger.info("Function {} created for user {}", savedFunction.getFunctionId(), targetUserId);
        return convertToDto(savedFunction);
    }

    private FunctionDto convertToDto(Function function) {
        return new FunctionDto(
                function.getFunctionId(),
                function.getName(),
                function.getExpression(),
                function.getUser().getUserId(),
                LocalDateTime.now()
        );
    }
}
