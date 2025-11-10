package db;


public interface UserRepository {
    // Только сигнатуры методов, без реализации!
    boolean createUser(String username, String email, String passwordHash);
    void getUserById(int userId);
    void getUserByUsername(String username);
    void getAllUsers();
    boolean updateUserEmail(int userId, String newEmail);
    boolean deleteUser(int userId);
}