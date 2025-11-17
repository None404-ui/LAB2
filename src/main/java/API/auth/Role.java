package API.auth;

public enum Role {
    ADMIN,      // Полный доступ
    USER,       // Только свои данные
    GUEST       // Только чтение
}