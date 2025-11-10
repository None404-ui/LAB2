package services;

import entities.ComputedPoint;
import entities.Function;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import repositories.ComputedPointRepository;
import repositories.FunctionRepository;
import repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {config.JpaConfig.class, config.DataSourceConfig.class})
@Transactional
public class FunctionSearchServiceTest {

    static {
        // Устанавливаем профиль ДО загрузки Spring контекста
        System.setProperty("spring.profiles.active", "test");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ComputedPointRepository computedPointRepository;

    @Autowired
    private FunctionSearchService searchService;

    private User testUser;
    private Function function1;
    private Function function2;
    private Function function3;

    @BeforeEach
    public void setUp() {
        
        // Генерация тестовых данных
        testUser = new User("testuser", "test@example.com", "password");
        userRepository.save(testUser);
        
        function1 = new Function("sin(x)", testUser, "Math.sin(x)");
        function2 = new Function("cos(x)", testUser, "Math.cos(x)");
        function3 = new Function("tan(x)", testUser, "Math.tan(x)");
        
        functionRepository.save(function1);
        functionRepository.save(function2);
        functionRepository.save(function3);
        
        // Добавление точек для function1
        computedPointRepository.save(new ComputedPoint(function1, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(function1, 1.0, 0.84));
        computedPointRepository.save(new ComputedPoint(function1, 2.0, 0.91));
    }

    @Test
    public void testFindSingle() {
        // Одиночный поиск
        Optional<Function> found = searchService.findSingle(testUser, function1.getFunctionId());
        
        assertTrue(found.isPresent());
        assertEquals("sin(x)", found.get().getName());
        
        // Попытка найти функцию другого пользователя
        User otherUser = new User("other", "other@example.com", "hash");
        userRepository.save(otherUser);
        
        Optional<Function> notFound = searchService.findSingle(otherUser, function1.getFunctionId());
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testSearchDepthFirst() {
        // Поиск в глубину
        List<Function> result = searchService.searchDepthFirst(testUser, function1.getFunctionId(), 3);
        
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(f -> f.getName().equals("sin(x)")));
    }

    @Test
    public void testSearchBreadthFirst() {
        // Поиск в ширину
        List<Function> result = searchService.searchBreadthFirst(testUser, function1.getFunctionId(), 3);
        
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().anyMatch(f -> f.getName().equals("sin(x)")));
    }

    @Test
    public void testSearchByHierarchy() {
        // Поиск по иерархии
        List<Function> result = searchService.searchByHierarchy(testUser);
        
        assertTrue(result.size() >= 3);
        assertTrue(result.stream().anyMatch(f -> f.getName().equals("sin(x)")));
        assertTrue(result.stream().anyMatch(f -> f.getName().equals("cos(x)")));
        assertTrue(result.stream().anyMatch(f -> f.getName().equals("tan(x)")));
    }

    @Test
    public void testFindMultipleWithFilters() {
        // Множественный поиск с фильтрацией
        Pageable pageable = PageRequest.of(0, 10);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        
        var result = searchService.findMultiple(testUser, "sin", null, pageable, sort);
        
        assertTrue(result.getTotalElements() >= 1);
        assertTrue(result.getContent().stream().anyMatch(f -> f.getName().contains("sin")));
    }

    @Test
    public void testFindMultipleWithSorting() {
        // Множественный поиск с сортировкой
        Pageable pageable = PageRequest.of(0, 10);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        
        var result = searchService.findMultiple(testUser, null, null, pageable, sort);
        
        assertTrue(result.getTotalElements() >= 3);
        List<Function> content = result.getContent();
        
        // Проверка сортировки
        for (int i = 1; i < content.size(); i++) {
            assertTrue(content.get(i-1).getName().compareTo(content.get(i).getName()) <= 0);
        }
    }

    @Test
    public void testFindMultipleWithPagination() {
        // Множественный поиск с пагинацией
        Pageable pageable = PageRequest.of(0, 2);
        
        var result = searchService.findMultiple(testUser, null, null, pageable, null);
        
        assertTrue(result.getTotalElements() >= 3);
        assertTrue(result.getContent().size() <= 2);
        assertTrue(result.getTotalPages() >= 2);
    }

    @Test
    public void testFindPointsWithSorting() {
        // Поиск точек с сортировкой по X
        Sort sort = Sort.by(Sort.Direction.ASC, "xValue");
        List<ComputedPoint> points = searchService.findPoints(function1, sort);
        
        assertTrue(points.size() >= 3);
        
        // Проверка сортировки
        for (int i = 1; i < points.size(); i++) {
            assertTrue(points.get(i-1).getXValue() <= points.get(i).getXValue());
        }
    }

    @Test
    public void testFindPointsWithSortingByY() {
        // Поиск точек с сортировкой по Y
        Sort sort = Sort.by(Sort.Direction.ASC, "yValue");
        List<ComputedPoint> points = searchService.findPoints(function1, sort);
        
        assertTrue(points.size() >= 3);
        
        // Проверка сортировки
        for (int i = 1; i < points.size(); i++) {
            assertTrue(points.get(i-1).getYValue() <= points.get(i).getYValue());
        }
    }

    @Test
    public void testFindPointsWithoutSorting() {
        // Поиск точек без сортировки
        List<ComputedPoint> points = searchService.findPoints(function1, null);
        
        assertTrue(points.size() >= 3);
    }

    @Test
    public void testFindMultipleWithExpressionFilter() {
        // Множественный поиск с фильтром по выражению
        Pageable pageable = PageRequest.of(0, 10);
        
        var result = searchService.findMultiple(testUser, null, "Math.sin", pageable, null);
        
        assertTrue(result.getTotalElements() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(f -> f.getExpression() != null && f.getExpression().contains("Math.sin")));
    }
}


