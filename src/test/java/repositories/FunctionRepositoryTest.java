package repositories;

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
public class FunctionRepositoryTest {

    static {
        // Устанавливаем профиль ДО загрузки Spring контекста
        System.setProperty("spring.profiles.active", "test");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        userRepository.save(testUser);
    }

    @Test
    public void testSaveAndFindFunction() {
        // Генерация данных
        Function function = new Function("sin(x)", testUser, "Math.sin(x)");
        Function saved = functionRepository.save(function);
        
        assertNotNull(saved.getFunctionId());
        assertEquals("sin(x)", saved.getName());
        assertEquals("Math.sin(x)", saved.getExpression());
        assertEquals(testUser.getUserId(), saved.getUser().getUserId());

        // Поиск по ID
        Optional<Function> found = functionRepository.findById(saved.getFunctionId());
        assertTrue(found.isPresent());
        assertEquals("sin(x)", found.get().getName());

        // Поиск по пользователю
        List<Function> userFunctions = functionRepository.findByUser(testUser);
        assertTrue(userFunctions.size() >= 1);
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("sin(x)")));
    }

    @Test
    public void testGenerateMultipleFunctions() {
        // Генерация нескольких функций
        Function func1 = new Function("sin(x)", testUser, "Math.sin(x)");
        Function func2 = new Function("cos(x)", testUser, "Math.cos(x)");
        Function func3 = new Function("tan(x)", testUser, "Math.tan(x)");
        
        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Поиск всех функций пользователя
        List<Function> allFunctions = functionRepository.findByUser(testUser);
        assertTrue(allFunctions.size() >= 3);

        // Поиск по имени
        List<Function> sinFunctions = functionRepository.findByUserAndNameContaining(testUser, "sin");
        assertTrue(sinFunctions.size() >= 1);
        assertTrue(sinFunctions.stream().anyMatch(f -> f.getName().equals("sin(x)")));
    }

    @Test
    public void testFindByUserAndNameContaining() {
        // Генерация данных
        functionRepository.save(new Function("sin(x)", testUser, "Math.sin(x)"));
        functionRepository.save(new Function("cos(x)", testUser, "Math.cos(x)"));
        functionRepository.save(new Function("sinh(x)", testUser, "Math.sinh(x)"));

        // Поиск функций содержащих "sin"
        List<Function> sinFunctions = functionRepository.findByUserAndNameContaining(testUser, "sin");
        assertTrue(sinFunctions.size() >= 2);
        assertTrue(sinFunctions.stream().anyMatch(f -> f.getName().equals("sin(x)")));
        assertTrue(sinFunctions.stream().anyMatch(f -> f.getName().equals("sinh(x)")));
    }

    @Test
    public void testFindByUserAndExpressionContaining() {
        // Генерация данных
        functionRepository.save(new Function("f1", testUser, "Math.sin(x)"));
        functionRepository.save(new Function("f2", testUser, "Math.cos(x)"));
        functionRepository.save(new Function("f3", testUser, "Math.sin(x) * 2"));

        // Поиск по выражению
        List<Function> mathSinFunctions = functionRepository.findByUserAndExpressionContaining(testUser, "Math.sin");
        assertTrue(mathSinFunctions.size() >= 2);
    }

    @Test
    public void testFindByFunctionIdAndUser() {
        // Генерация данных
        Function function = new Function("test", testUser, "x");
        Function saved = functionRepository.save(function);
        
        // Поиск с проверкой доступа
        Optional<Function> found = functionRepository.findByFunctionIdAndUser(saved.getFunctionId(), testUser);
        assertTrue(found.isPresent());
        assertEquals("test", found.get().getName());

        // Попытка найти функцию другого пользователя
        User otherUser = new User("other", "other@example.com", "hash");
        userRepository.save(otherUser);
        
        Optional<Function> notFound = functionRepository.findByFunctionIdAndUser(saved.getFunctionId(), otherUser);
        assertFalse(notFound.isPresent());
    }

    @Test
    public void testCountByUser() {
        // Генерация данных
        functionRepository.save(new Function("f1", testUser, "x"));
        functionRepository.save(new Function("f2", testUser, "x*x"));
        functionRepository.save(new Function("f3", testUser, "x*x*x"));

        long count = functionRepository.countByUser(testUser);
        assertTrue(count >= 3);
    }

    @Test
    public void testFindByUserOrderByName() {
        // Генерация данных
        functionRepository.save(new Function("zebra", testUser, "x"));
        functionRepository.save(new Function("alpha", testUser, "x"));
        functionRepository.save(new Function("beta", testUser, "x"));

        // Поиск с сортировкой
        List<Function> sorted = functionRepository.findByUserOrderByName(testUser);
        assertTrue(sorted.size() >= 3);
        // Проверка сортировки
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i-1).getName().compareTo(sorted.get(i).getName()) <= 0);
        }
    }

    @Test
    public void testDeleteFunction() {
        // Генерация данных
        Function function = new Function("todelete", testUser, "x");
        Function saved = functionRepository.save(function);
        Integer functionId = saved.getFunctionId();
        
        // Удаление
        functionRepository.delete(saved);
        
        // Проверка удаления
        Optional<Function> found = functionRepository.findById(functionId);
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteAllFunctions() {
        // Генерация данных
        functionRepository.save(new Function("f1", testUser, "x"));
        functionRepository.save(new Function("f2", testUser, "x*x"));
        
        long countBefore = functionRepository.countByUser(testUser);
        assertTrue(countBefore >= 2);
        
        // Удаление всех функций пользователя
        List<Function> functions = functionRepository.findByUser(testUser);
        functionRepository.deleteAll(functions);
        
        // Проверка
        assertEquals(0, functionRepository.countByUser(testUser));
    }
}


