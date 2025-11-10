package repositories;

import entities.ComputedPoint;
import entities.Function;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {config.JpaConfig.class, config.DataSourceConfig.class})
@Transactional
public class ComputedPointRepositoryTest {

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

    private User testUser;
    private Function testFunction;

    @BeforeEach
    public void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        userRepository.save(testUser);
        
        testFunction = new Function("test", testUser, "x*x");
        functionRepository.save(testFunction);
    }

    @Test
    public void testSaveAndFindComputedPoint() {
        // Генерация данных
        ComputedPoint point = new ComputedPoint(testFunction, 1.0, 1.0);
        ComputedPoint saved = computedPointRepository.save(point);
        
        assertEquals(1.0, saved.getXValue());
        assertEquals(1.0, saved.getYValue());
        assertEquals(testFunction.getFunctionId(), saved.getFunction().getFunctionId());

        // Поиск по функции
        List<ComputedPoint> points = computedPointRepository.findByFunction(testFunction);
        assertTrue(points.size() >= 1);
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(1.0)));
    }

    @Test
    public void testGenerateMultiplePoints() {
        // Генерация нескольких точек
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.0, 4.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 3.0, 9.0));

        // Поиск всех точек функции
        List<ComputedPoint> allPoints = computedPointRepository.findByFunction(testFunction);
        assertEquals(4, allPoints.size());

        // Проверка данных
        assertTrue(allPoints.stream().anyMatch(p -> p.getXValue().equals(0.0) && p.getYValue().equals(0.0)));
        assertTrue(allPoints.stream().anyMatch(p -> p.getXValue().equals(2.0) && p.getYValue().equals(4.0)));
    }

    @Test
    public void testFindByFunctionOrderByXValue() {
        // Генерация данных в случайном порядке
        computedPointRepository.save(new ComputedPoint(testFunction, 3.0, 9.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.0, 4.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));

        // Поиск с сортировкой по X
        List<ComputedPoint> sorted = computedPointRepository.findByFunctionOrderByXValue(testFunction);
        assertEquals(4, sorted.size());
        
        // Проверка сортировки
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i-1).getXValue() <= sorted.get(i).getXValue());
        }
        assertEquals(0.0, sorted.get(0).getXValue());
        assertEquals(3.0, sorted.get(3).getXValue());
    }

    @Test
    public void testFindByFunctionAndXValue() {
        // Генерация данных
        computedPointRepository.save(new ComputedPoint(testFunction, 1.5, 2.25));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.5, 6.25));

        // Поиск по конкретному X
        Optional<ComputedPoint> found = computedPointRepository.findByFunctionAndXValue(testFunction, 1.5);
        assertTrue(found.isPresent());
        assertEquals(2.25, found.get().getYValue());

        // Поиск несуществующей точки
        Optional<ComputedPoint> notFound = computedPointRepository.findByFunctionAndXValue(testFunction, 99.0);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindByFunctionAndXValueBetween() {
        // Генерация данных
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.0, 4.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 3.0, 9.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 4.0, 16.0));

        // Поиск точек в диапазоне
        List<ComputedPoint> inRange = computedPointRepository.findByFunctionAndXValueBetween(testFunction, 1.0, 3.0);
        assertTrue(inRange.size() >= 3);
        assertTrue(inRange.stream().allMatch(p -> p.getXValue() >= 1.0 && p.getXValue() <= 3.0));
    }

    @Test
    public void testCountByFunction() {
        // Генерация данных
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.0, 4.0));

        long count = computedPointRepository.countByFunction(testFunction);
        assertTrue(count >= 3);
    }

    @Test
    public void testDeleteComputedPoint() {
        // Генерация данных
        ComputedPoint point = new ComputedPoint(testFunction, 5.0, 25.0);
        computedPointRepository.save(point);
        
        long countBefore = computedPointRepository.countByFunction(testFunction);
        
        // Удаление
        computedPointRepository.delete(point);
        
        // Проверка удаления
        long countAfter = computedPointRepository.countByFunction(testFunction);
        assertEquals(countBefore - 1, countAfter);
        
        Optional<ComputedPoint> found = computedPointRepository.findByFunctionAndXValue(testFunction, 5.0);
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteByFunction() {
        // Генерация данных
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 2.0, 4.0));
        
        long countBefore = computedPointRepository.countByFunction(testFunction);
        assertTrue(countBefore >= 3);
        
        // Удаление всех точек функции
        computedPointRepository.deleteByFunction(testFunction);
        
        // Проверка
        assertEquals(0, computedPointRepository.countByFunction(testFunction));
    }

    @Test
    public void testMultipleFunctionsWithPoints() {
        // Генерация второй функции
        Function function2 = new Function("cos(x)", testUser, "Math.cos(x)");
        functionRepository.save(function2);
        
        // Генерация точек для обеих функций
        computedPointRepository.save(new ComputedPoint(testFunction, 0.0, 0.0));
        computedPointRepository.save(new ComputedPoint(testFunction, 1.0, 1.0));
        computedPointRepository.save(new ComputedPoint(function2, 0.0, 1.0));
        computedPointRepository.save(new ComputedPoint(function2, 1.0, 0.54));

        // Проверка изоляции данных
        List<ComputedPoint> points1 = computedPointRepository.findByFunction(testFunction);
        List<ComputedPoint> points2 = computedPointRepository.findByFunction(function2);
        
        assertEquals(2, points1.size());
        assertEquals(2, points2.size());
        assertTrue(points1.stream().allMatch(p -> p.getFunction().equals(testFunction)));
        assertTrue(points2.stream().allMatch(p -> p.getFunction().equals(function2)));
    }
}


