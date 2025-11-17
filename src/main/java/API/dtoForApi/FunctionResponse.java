package API.dtoForApi;

import java.time.LocalDateTime;

public class FunctionResponse {
    private Integer functionId;
    private String name;
    private String expression;
    private Integer userId;
    private LocalDateTime createdAt;

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}