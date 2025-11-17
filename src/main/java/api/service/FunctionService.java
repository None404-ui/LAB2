package api.service;

import api.dto.CreateFunctionRequest;
import api.dto.FunctionDto;
import entities.Function;
import entities.User;
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

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FunctionDto> getAllFunctions() {
        return functionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FunctionDto> getFunctionsByUserId(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return functionRepository.findByUser(user.get()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FunctionDto createFunction(CreateFunctionRequest request) {
        // Проверяем, существует ли пользователь
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Function function = new Function();
        function.setName(request.getName());
        function.setExpression(request.getExpression());
        function.setUser(user.get());

        Function savedFunction = functionRepository.save(function);
        return convertToDto(savedFunction);
    }

    private FunctionDto convertToDto(Function function) {
        return new FunctionDto(
                function.getFunctionId(),
                function.getName(),
                function.getExpression(),
                function.getUser().getUserId(),
                LocalDateTime.now() // В реальном приложении нужно брать из БД
        );
    }
}
