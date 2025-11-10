package dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserDto {
    private static final Logger logger = LoggerFactory.getLogger(UserDto.class);

    private Integer userId;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Конструкторы
    public UserDto() {
        logger.debug("Creating empty UserDto");
    }

    public UserDto(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        logger.debug("Creating UserDto with username: {}", username);
    }

    // Геттеры и сеттеры
    public Integer getUserId() {
        logger.trace("Getting userId: {}", userId);
        return userId;
    }

    public void setUserId(Integer userId) {
        logger.debug("Setting userId from {} to {}", this.userId, userId);
        this.userId = userId;
    }

    public String getUsername() {
        logger.trace("Getting username: {}", username);
        return username;
    }

    public void setUsername(String username) {
        logger.debug("Setting username from {} to {}", this.username, username);
        this.username = username;
    }

    public String getEmail() {
        logger.trace("Getting email: {}", email);
        return email;
    }

    public void setEmail(String email) {
        logger.debug("Setting email from {} to {}", this.email, email);
        this.email = email;
    }

    public String getPasswordHash() {
        logger.trace("Getting passwordHash");
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        logger.debug("Setting passwordHash");
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        logger.trace("Getting createdAt: {}", createdAt);
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        logger.debug("Setting createdAt from {} to {}", this.createdAt, createdAt);
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        logger.trace("Getting updatedAt: {}", updatedAt);
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        logger.debug("Setting updatedAt from {} to {}", this.updatedAt, updatedAt);
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            logger.trace("UserDto equals: same object");
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            logger.trace("UserDto equals: different classes or null");
            return false;
        }
        UserDto userDto = (UserDto) o;
        boolean isEqual = Objects.equals(userId, userDto.userId) &&
                Objects.equals(username, userDto.username) &&
                Objects.equals(email, userDto.email);
        logger.debug("UserDto equals result: {} for userId {}", isEqual, userId);
        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(userId, username, email);
        logger.trace("UserDto hashCode: {} for userId {}", hash, userId);
        return hash;
    }

    @Override
    public String toString() {
        String str = "UserDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
        logger.trace("UserDto toString: {}", str);
        return str;
    }
}