package dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class FunctionDto {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDto.class);

    private Integer functionId;
    private String name;
    private String expression;
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Конструкторы
    public FunctionDto() {
        logger.debug("Creating empty FunctionDto");
    }

    public FunctionDto(String name, String expression, Integer userId) {
        this.name = name;
        this.expression = expression;
        this.userId = userId;
        logger.debug("Creating FunctionDto with name: {}, userId: {}", name, userId);
    }

    // Геттеры и сеттеры
    public Integer getFunctionId() {
        logger.trace("Getting functionId: {}", functionId);
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        logger.debug("Setting functionId from {} to {}", this.functionId, functionId);
        this.functionId = functionId;
    }

    public String getName() {
        logger.trace("Getting name: {}", name);
        return name;
    }

    public void setName(String name) {
        logger.debug("Setting name from {} to {}", this.name, name);
        this.name = name;
    }

    public String getExpression() {
        logger.trace("Getting expression: {}", expression);
        return expression;
    }

    public void setExpression(String expression) {
        logger.debug("Setting expression from {} to {}", this.expression, expression);
        this.expression = expression;
    }

    public Integer getUserId() {
        logger.trace("Getting userId: {}", userId);
        return userId;
    }

    public void setUserId(Integer userId) {
        logger.debug("Setting userId from {} to {}", this.userId, userId);
        this.userId = userId;
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
            logger.trace("FunctionDto equals: same object");
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            logger.trace("FunctionDto equals: different classes or null");
            return false;
        }
        FunctionDto that = (FunctionDto) o;
        boolean isEqual = Objects.equals(functionId, that.functionId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(userId, that.userId);
        logger.debug("FunctionDto equals result: {} for functionId {}", isEqual, functionId);
        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(functionId, name, userId);
        logger.trace("FunctionDto hashCode: {} for functionId {}", hash, functionId);
        return hash;
    }

    @Override
    public String toString() {
        String str = "FunctionDto{" +
                "functionId=" + functionId +
                ", name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                ", userId=" + userId +
                '}';
        logger.trace("FunctionDto toString: {}", str);
        return str;
    }
}