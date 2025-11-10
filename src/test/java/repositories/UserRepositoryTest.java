package repositories;

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
public class UserRepositoryTest {

    static {
        // Устанавливаем профиль ДО загрузки Spring контекста
        System.setProperty("spring.profiles.active", "test");
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        // Генерация данных
        User user = new User("testuser", "test@example.com", "hashedpassword123");
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser.getUserId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());

        // Поиск по ID
        Optional<User> found = userRepository.findById(savedUser.getUserId());
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());

        // Поиск по username
        found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());

        // Поиск по email
        found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    public void testGenerateMultipleUsers() {
        // Генерация нескольких пользователей
        User user1 = new User("user1", "user1@example.com", "hash1");
        User user2 = new User("user2", "user2@example.com", "hash2");
        User user3 = new User("user3", "user3@example.com", "hash3");
        
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Поиск всех пользователей
        List<User> allUsers = userRepository.findAll();
        assertTrue(allUsers.size() >= 3);

        // Поиск по username
        Optional<User> found = userRepository.findByUsername("user2");
        assertTrue(found.isPresent());
        assertEquals("user2@example.com", found.get().getEmail());
    }

    @Test
    public void testFindByUsernameOrEmail() {
        // Генерация данных
        User user = new User("testuser", "test@example.com", "hash");
        userRepository.save(user);
        
        // Поиск по username
        Optional<User> found = userRepository.findByUsernameOrEmail("testuser", "other@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());

        // Поиск по email
        found = userRepository.findByUsernameOrEmail("otheruser", "test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    public void testExistsByUsername() {
        // Генерация данных
        User user = new User("existinguser", "existing@example.com", "hash");
        userRepository.save(user);
        
        assertTrue(userRepository.existsByUsername("existinguser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    public void testDeleteUser() {
        // Генерация данных
        User user = new User("todelete", "delete@example.com", "hash");
        User saved = userRepository.save(user);
        Integer userId = saved.getUserId();
        
        // Удаление
        userRepository.delete(saved);
        
        // Проверка удаления
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteAll() {
        // Генерация данных
        userRepository.save(new User("user1", "u1@example.com", "hash1"));
        userRepository.save(new User("user2", "u2@example.com", "hash2"));
        
        long countBefore = userRepository.count();
        assertTrue(countBefore >= 2);
        
        // Удаление всех
        userRepository.deleteAll();
        
        // Проверка
        assertEquals(0, userRepository.count());
    }
}


