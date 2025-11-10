package db.model;

import java.sql.Timestamp;
import java.util.Objects;

public class User {
    private final int userId;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Timestamp createdAt;

    public User(int userId, String username, String email, String passwordHash, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Timestamp getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s'}", userId, username, email);
    }
}