package API.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // Хранилище пользователей (в памяти)
    private static final Map<String, UserPrincipal> users = new HashMap<>();

    static {
        // Предустановленные пользователи
        users.put("admin", new UserPrincipal("admin", Role.ADMIN));
        users.put("user1", new UserPrincipal("user1", Role.USER));
        users.put("guest", new UserPrincipal("guest", Role.GUEST));
        logger.info("AuthService initialized with {} users", users.size());
    }

    public static UserPrincipal authenticate(String username, String password) {
        logger.debug("Attempting authentication for user: {}", username);

        // Простая проверка (в реальном приложении - хэширование паролей)
        if (users.containsKey(username) && "123".equals(password)) {
            UserPrincipal user = users.get(username);
            logger.info("User authenticated successfully: {} with role: {}", username, user.getRole());
            return user;
        }

        logger.warn("Authentication failed for user: {}", username);
        return null;
    }

    public static boolean registerUser(String username, String password, Role role) {
        logger.debug("Registering new user: {} with role: {}", username, role);

        if (users.containsKey(username)) {
            logger.warn("User already exists: {}", username);
            return false;
        }

        users.put(username, new UserPrincipal(username, role));
        logger.info("User registered successfully: {} with role: {}", username, role);
        return true;
    }

    public static UserPrincipal getUser(String username) {
        return users.get(username);
    }
}