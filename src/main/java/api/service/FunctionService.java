package api.service;

import api.dto.CreateFunctionFromArraysRequest;
import api.dto.CreateFunctionFromMathRequest;
import api.dto.CreateFunctionRequest;
import api.dto.FunctionDto;
import api.dto.InsertPointRequest;
import api.security.CurrentUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Function;
import entities.RoleName;
import entities.User;
import functions.*;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;
import operations.TabulatedFunctionOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.FunctionRepository;
import repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Map для доступных математических функций
    private final Map<String, MathFunction> mathFunctions = createMathFunctionsMap();

    private Map<String, MathFunction> createMathFunctionsMap() {
        Map<String, MathFunction> map = new LinkedHashMap<>();
        map.put("Квадратичная функция", new SqrFunction());
        map.put("Тождественная функция", new IdentityFunction());
        map.put("Единичная функция", new UnitFunction());
        map.put("Нулевая функция", new ZeroFunction());
        map.put("Константная функция", new ConstantFunction(1.0));
        return map;
    }

    private TabulatedFunctionFactory getFactory(String functionType) {
        return "LINKED_LIST".equals(functionType)
                ? new LinkedListTabulatedFunctionFactory()
                : new ArrayTabulatedFunctionFactory();
    }

    public List<String> getAvailableMathFunctions() {
        return new ArrayList<>(mathFunctions.keySet());
    }

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
        function.setUser(user.get());

        Function savedFunction = functionRepository.save(function);
        logger.info("Function {} created for user {}", savedFunction.getFunctionId(), targetUserId);
        return convertToDto(savedFunction);
    }

    public FunctionDto createFunctionFromArrays(CreateFunctionFromArraysRequest request) {
        logger.info("createFunctionFromArrays called with: {}", request);
        
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Optional<User> user = userRepository.findById(targetUserId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        double[] xValues = request.getXValues();
        double[] yValues = request.getYValues();

        // Базовая валидация
        if (xValues == null || yValues == null) {
            logger.error("xValues or yValues is null! xValues={}, yValues={}", xValues, yValues);
            throw new IllegalArgumentException("Массивы x и y не могут быть null");
        }
        
        if (xValues.length == 0 || yValues.length == 0) {
            throw new IllegalArgumentException("Массивы x и y не могут быть пустыми");
        }

        // Выбор фабрики и валидация данных через создание функции
        TabulatedFunctionFactory factory = getFactory(request.getFactoryType());
        try {
            factory.create(xValues, yValues);
        } catch (Exception validationError) {
            logger.error("Validation error in constructor: {}", validationError.getMessage());
            throw new IllegalArgumentException(validationError.getMessage());
        }

        // Сохранение в БД
        try {
            Function function = new Function();
            function.setName(request.getName());
            function.setUser(user.get());
            function.setFunctionType(request.getFactoryType() != null ? request.getFactoryType() : "ARRAY");
            function.setXValues(objectMapper.writeValueAsString(xValues));
            function.setYValues(objectMapper.writeValueAsString(yValues));
            function.setCount(xValues.length);

            Function savedFunction = functionRepository.save(function);
            logger.info("Function {} created from arrays for user {}", savedFunction.getFunctionId(), targetUserId);
            return convertToDto(savedFunction);
        } catch (Exception e) {
            logger.error("Error creating function from arrays", e);
            throw new RuntimeException("Ошибка при создании функции: " + e.getMessage());
        }
    }

    public FunctionDto createFunctionFromMath(CreateFunctionFromMathRequest request) {
        logger.info("createFunctionFromMath: name={}, type={}, xFrom={}, xTo={}, count={}, factory={}", 
                   request.getName(), request.getMathFunctionType(), 
                   request.getXFrom(), request.getXTo(), request.getCount(), request.getFactoryType());
        
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Optional<User> user = userRepository.findById(targetUserId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // Получение математической функции (валидация происходит в конструкторах)
        MathFunction mathFunction = mathFunctions.get(request.getMathFunctionType());
        logger.info("Found mathFunction: {}", mathFunction != null ? mathFunction.getClass().getSimpleName() : "NULL");
        if (mathFunction == null) {
            logger.error("Available math functions: {}", mathFunctions.keySet());
            throw new IllegalArgumentException("Неизвестный тип математической функции: " + request.getMathFunctionType());
        }

        // Выбор фабрики и создание табулированной функции
        TabulatedFunctionFactory factory = getFactory(request.getFactoryType());
        TabulatedFunction tabulatedFunction = factory.create(
                mathFunction, 
                request.getXFrom(), 
                request.getXTo(), 
                request.getCount()
        );

        // Извлечение массивов x и y
        double[] xValues = new double[tabulatedFunction.getCount()];
        double[] yValues = new double[tabulatedFunction.getCount()];
        int i = 0;
        for (Point point : tabulatedFunction) {
            xValues[i] = point.x;
            yValues[i] = point.y;
            i++;
        }

        // Сохранение в БД
        try {
            Function function = new Function();
            function.setName(request.getName());
            function.setUser(user.get());
            function.setFunctionType(request.getFactoryType() != null ? request.getFactoryType() : "ARRAY");
            function.setXValues(objectMapper.writeValueAsString(xValues));
            function.setYValues(objectMapper.writeValueAsString(yValues));
            function.setCount(xValues.length);

            Function savedFunction = functionRepository.save(function);
            logger.info("Function {} created from math function for user {}", savedFunction.getFunctionId(), targetUserId);
            return convertToDto(savedFunction);
        } catch (Exception e) {
            logger.error("Error creating function from math", e);
            throw new RuntimeException("Ошибка при создании функции: " + e.getMessage());
        }
    }

    public FunctionDto getFunctionById(Integer functionId) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        boolean isAdmin = currentUserService.hasRole(RoleName.ROLE_ADMIN);
        
        Optional<Function> functionOpt = functionRepository.findById(functionId);
        if (functionOpt.isEmpty()) {
            throw new IllegalArgumentException("Функция не найдена");
        }
        
        Function function = functionOpt.get();
        // Админ может видеть все функции, обычный пользователь - только свои
        if (!isAdmin && !function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        return convertToDto(function);
    }

    public double applyFunction(Integer functionId, double x) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        boolean isAdmin = currentUserService.hasRole(RoleName.ROLE_ADMIN);
        
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Функция не найдена"));
        
        if (!isAdmin && !function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        try {
            double[] xValues = objectMapper.readValue(function.getXValues(), double[].class);
            double[] yValues = objectMapper.readValue(function.getYValues(), double[].class);
            
            TabulatedFunctionFactory factory = getFactory(function.getFunctionType());
            TabulatedFunction tabFunc = factory.create(xValues, yValues);
            return tabFunc.apply(x);
            
        } catch (Exception e) {
            logger.error("Error applying function", e);
            throw new RuntimeException("Ошибка при вычислении значения: " + e.getMessage());
        }
    }

    public FunctionDto insertPoint(Integer functionId, InsertPointRequest request) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Функция не найдена"));
        
        if (!function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        try {
            double[] xValues = objectMapper.readValue(function.getXValues(), double[].class);
            double[] yValues = objectMapper.readValue(function.getYValues(), double[].class);
            
            TabulatedFunctionFactory factory = getFactory(function.getFunctionType());
            TabulatedFunction tabFunc = factory.create(xValues, yValues);
            
            // Проверяем что функция поддерживает Insertable
            if (!(tabFunc instanceof Insertable)) {
                throw new IllegalArgumentException("Функция не поддерживает вставку точек");
            }
            
            ((Insertable) tabFunc).insert(request.getX(), request.getY());
            
            // Извлекаем обновленные массивы
            double[] newXValues = new double[tabFunc.getCount()];
            double[] newYValues = new double[tabFunc.getCount()];
            int i = 0;
            for (Point point : tabFunc) {
                newXValues[i] = point.x;
                newYValues[i] = point.y;
                i++;
            }
            
            function.setXValues(objectMapper.writeValueAsString(newXValues));
            function.setYValues(objectMapper.writeValueAsString(newYValues));
            function.setCount(newXValues.length);
            
            Function saved = functionRepository.save(function);
            logger.info("Point inserted into function {}", functionId);
            return convertToDto(saved);
            
        } catch (Exception e) {
            logger.error("Error inserting point", e);
            throw new RuntimeException("Ошибка при вставке точки: " + e.getMessage());
        }
    }

    public FunctionDto removePoint(Integer functionId, int index) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Функция не найдена"));
        
        if (!function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        try {
            double[] xValues = objectMapper.readValue(function.getXValues(), double[].class);
            double[] yValues = objectMapper.readValue(function.getYValues(), double[].class);
            
            if (xValues.length <= 2) {
                throw new IllegalArgumentException("Нельзя удалить точку - минимум 2 точки");
            }
            
            if (index < 0 || index >= xValues.length) {
                throw new IllegalArgumentException("Некорректный индекс");
            }
            
            TabulatedFunctionFactory factory = getFactory(function.getFunctionType());
            TabulatedFunction tabFunc = factory.create(xValues, yValues);
            
            // Проверяем что функция поддерживает Removable
            if (!(tabFunc instanceof Removable)) {
                throw new IllegalArgumentException("Функция не поддерживает удаление точек");
            }
            
            ((Removable) tabFunc).remove(index);
            
            // Извлекаем обновленные массивы
            double[] newXValues = new double[tabFunc.getCount()];
            double[] newYValues = new double[tabFunc.getCount()];
            int i = 0;
            for (Point point : tabFunc) {
                newXValues[i] = point.x;
                newYValues[i] = point.y;
                i++;
            }
            
            function.setXValues(objectMapper.writeValueAsString(newXValues));
            function.setYValues(objectMapper.writeValueAsString(newYValues));
            function.setCount(newXValues.length);
            
            Function saved = functionRepository.save(function);
            logger.info("Point removed from function {}", functionId);
            return convertToDto(saved);
            
        } catch (Exception e) {
            logger.error("Error removing point", e);
            throw new RuntimeException("Ошибка при удалении точки: " + e.getMessage());
        }
    }

    public FunctionDto performOperation(Integer functionId1, Integer functionId2, 
                                       String operation, String resultName, String factoryType) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        // Получаем функции
        Function func1 = functionRepository.findById(functionId1)
                .orElseThrow(() -> new IllegalArgumentException("Функция 1 не найдена"));
        Function func2 = functionRepository.findById(functionId2)
                .orElseThrow(() -> new IllegalArgumentException("Функция 2 не найдена"));
        
        // Проверяем права доступа
        if (!func1.getUser().getUserId().equals(targetUserId) || 
            !func2.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к функциям");
        }
        
        try {
            // Создаем TabulatedFunction из сохраненных данных
            double[] x1 = objectMapper.readValue(func1.getXValues(), double[].class);
            double[] y1 = objectMapper.readValue(func1.getYValues(), double[].class);
            double[] x2 = objectMapper.readValue(func2.getXValues(), double[].class);
            double[] y2 = objectMapper.readValue(func2.getYValues(), double[].class);
            
            TabulatedFunctionFactory factory = "LINKED_LIST".equals(factoryType)
                    ? new LinkedListTabulatedFunctionFactory()
                    : new ArrayTabulatedFunctionFactory();
            
            TabulatedFunction tabFunc1 = factory.create(x1, y1);
            TabulatedFunction tabFunc2 = factory.create(x2, y2);
            
            // Выполняем операцию
            TabulatedFunctionOperationService operationService = new TabulatedFunctionOperationService(factory);
            TabulatedFunction result;
            
            switch (operation.toUpperCase()) {
                case "ADD":
                    result = operationService.add(tabFunc1, tabFunc2);
                    break;
                case "SUBTRACT":
                    result = operationService.subtract(tabFunc1, tabFunc2);
                    break;
                case "MULTIPLY":
                    result = operationService.multiply(tabFunc1, tabFunc2);
                    break;
                case "DIVIDE":
                    result = operationService.divide(tabFunc1, tabFunc2);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестная операция: " + operation);
            }
            
            // Сохраняем результат
            double[] xResult = new double[result.getCount()];
            double[] yResult = new double[result.getCount()];
            int i = 0;
            for (Point point : result) {
                xResult[i] = point.x;
                yResult[i] = point.y;
                i++;
            }
            
            Optional<User> user = userRepository.findById(targetUserId);
            Function resultFunction = new Function();
            resultFunction.setName(resultName);
            resultFunction.setUser(user.get());
            resultFunction.setFunctionType(factoryType != null ? factoryType : "ARRAY");
            resultFunction.setXValues(objectMapper.writeValueAsString(xResult));
            resultFunction.setYValues(objectMapper.writeValueAsString(yResult));
            resultFunction.setCount(xResult.length);
            
            Function saved = functionRepository.save(resultFunction);
            logger.info("Function operation {} completed, result saved with ID: {}", operation, saved.getFunctionId());
            return convertToDto(saved);
            
        } catch (Exception e) {
            logger.error("Error performing operation", e);
            throw new RuntimeException("Ошибка при выполнении операции: " + e.getMessage());
        }
    }

    public FunctionDto differentiate(Integer functionId, String resultName, String factoryType) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Функция не найдена"));
        
        if (!function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        try {
            double[] x = objectMapper.readValue(function.getXValues(), double[].class);
            double[] y = objectMapper.readValue(function.getYValues(), double[].class);
            
            TabulatedFunctionFactory factory = "LINKED_LIST".equals(factoryType)
                    ? new LinkedListTabulatedFunctionFactory()
                    : new ArrayTabulatedFunctionFactory();
            
            TabulatedFunction tabFunc = factory.create(x, y);
            
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);
            TabulatedFunction result = operator.derive(tabFunc);
            
            // Сохраняем результат
            double[] xResult = new double[result.getCount()];
            double[] yResult = new double[result.getCount()];
            int i = 0;
            for (Point point : result) {
                xResult[i] = point.x;
                yResult[i] = point.y;
                i++;
            }
            
            Optional<User> user = userRepository.findById(targetUserId);
            Function resultFunction = new Function();
            resultFunction.setName(resultName);
            resultFunction.setUser(user.get());
            resultFunction.setFunctionType(factoryType != null ? factoryType : "ARRAY");
            resultFunction.setXValues(objectMapper.writeValueAsString(xResult));
            resultFunction.setYValues(objectMapper.writeValueAsString(yResult));
            resultFunction.setCount(xResult.length);
            
            Function saved = functionRepository.save(resultFunction);
            logger.info("Function differentiated, result saved with ID: {}", saved.getFunctionId());
            return convertToDto(saved);
            
        } catch (Exception e) {
            logger.error("Error differentiating function", e);
            throw new RuntimeException("Ошибка при дифференцировании: " + e.getMessage());
        }
    }

    public FunctionDto updateYValues(Integer functionId, double[] newYValues) {
        Integer targetUserId = currentUserService.getCurrentUserId();
        
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Функция не найдена"));
        
        if (!function.getUser().getUserId().equals(targetUserId)) {
            throw new IllegalArgumentException("Нет доступа к этой функции");
        }
        
        try {
            double[] xValues = objectMapper.readValue(function.getXValues(), double[].class);
            
            if (xValues.length != newYValues.length) {
                throw new IllegalArgumentException("Количество Y значений не соответствует количеству X");
            }
            
            function.setYValues(objectMapper.writeValueAsString(newYValues));
            Function saved = functionRepository.save(function);
            logger.info("Function {} Y values updated", functionId);
            return convertToDto(saved);
            
        } catch (Exception e) {
            logger.error("Error updating Y values", e);
            throw new RuntimeException("Ошибка при обновлении значений: " + e.getMessage());
        }
    }

    private FunctionDto convertToDto(Function function) {
        try {
            double[] xValues;
            double[] yValues;
            Integer count = function.getCount();
            String functionType = function.getFunctionType();
            
            // Проверяем есть ли новые данные (xValues, yValues)
            logger.debug("Converting function {}: xValues={}, yValues={}", 
                        function.getFunctionId(), function.getXValues(), function.getYValues());
            if (function.getXValues() != null && function.getYValues() != null) {
                xValues = objectMapper.readValue(function.getXValues(), double[].class);
                yValues = objectMapper.readValue(function.getYValues(), double[].class);
                logger.debug("Parsed xValues: {}, yValues: {}", Arrays.toString(xValues), Arrays.toString(yValues));
            } else if (function.getExpression() != null && !function.getExpression().isEmpty()) {
                // Старая функция с expression - генерируем точки
                // Парсим expression типа "f(x)=x^2" или просто используем имя
                xValues = new double[]{0, 1, 2, 3, 4, 5};
                yValues = generateYValuesFromExpression(function.getExpression(), xValues);
                count = xValues.length;
                if (functionType == null) {
                    functionType = "LINKED_LIST"; // По умолчанию для старых функций
                }
            } else {
                // Нет данных - пустые массивы
                xValues = new double[0];
                yValues = new double[0];
            }
            
            // Проверяем, реализует ли функция Insertable и Removable
            boolean isInsertable = false;
            boolean isRemovable = false;
            
            if (xValues.length >= 2 && yValues.length >= 2) {
                try {
                    TabulatedFunctionFactory factory = getFactory(functionType);
                    TabulatedFunction tempFunc = factory.create(xValues, yValues);
                    isInsertable = tempFunc instanceof Insertable;
                    isRemovable = tempFunc instanceof Removable;
                } catch (Exception ex) {
                    logger.warn("Cannot determine Insertable/Removable for function {}: {}", function.getFunctionId(), ex.getMessage());
                }
            }
            
            return new FunctionDto(
                    function.getFunctionId(),
                    function.getName(),
                    functionType,
                    xValues,
                    yValues,
                    count != null ? count : xValues.length,
                    function.getUser().getUserId(),
                    LocalDateTime.now(),
                    isInsertable,
                    isRemovable
            );
        } catch (Exception e) {
            logger.error("Error converting function to DTO", e);
            throw new RuntimeException("Ошибка при преобразовании функции");
        }
    }
    
    /**
     * Генерирует Y значения на основе expression (для старых функций)
     */
    private double[] generateYValuesFromExpression(String expression, double[] xValues) {
        double[] yValues = new double[xValues.length];
        
        // Простой парсер для базовых выражений
        String expr = expression.toLowerCase().replace(" ", "");
        
        // Убираем "f(x)=" если есть
        if (expr.contains("=")) {
            expr = expr.substring(expr.indexOf("=") + 1);
        }
        
        for (int i = 0; i < xValues.length; i++) {
            double x = xValues[i];
            try {
                if (expr.equals("x^2") || expr.equals("x*x")) {
                    yValues[i] = x * x;
                } else if (expr.equals("x^3") || expr.equals("x*x*x")) {
                    yValues[i] = x * x * x;
                } else if (expr.equals("x")) {
                    yValues[i] = x;
                } else if (expr.equals("sin(x)")) {
                    yValues[i] = Math.sin(x);
                } else if (expr.equals("cos(x)")) {
                    yValues[i] = Math.cos(x);
                } else if (expr.equals("exp(x)") || expr.equals("e^x")) {
                    yValues[i] = Math.exp(x);
                } else if (expr.equals("sqrt(x)")) {
                    yValues[i] = Math.sqrt(x);
                } else if (expr.equals("1") || expr.equals("1.0")) {
                    yValues[i] = 1.0;
                } else if (expr.equals("0") || expr.equals("0.0")) {
                    yValues[i] = 0.0;
                } else {
                    // По умолчанию - линейная функция
                    yValues[i] = x;
                }
            } catch (Exception e) {
                yValues[i] = x; // Fallback
            }
        }
        
        return yValues;
    }
}
