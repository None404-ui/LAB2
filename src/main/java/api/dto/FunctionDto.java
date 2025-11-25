package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class FunctionDto {
    private Integer functionId;
    private String name;
    private String functionType;
    
    @JsonProperty("xValues")
    private double[] xValues;
    
    @JsonProperty("yValues")
    private double[] yValues;
    
    private Integer count;
    private Integer userId;
    private LocalDateTime createdAt;
    
    @JsonProperty("isInsertable")
    private boolean isInsertable;
    
    @JsonProperty("isRemovable")
    private boolean isRemovable;

    public FunctionDto() {}

    public FunctionDto(Integer functionId, String name, String functionType, double[] xValues, double[] yValues, Integer count, Integer userId, LocalDateTime createdAt, boolean isInsertable, boolean isRemovable) {
        this.functionId = functionId;
        this.name = name;
        this.functionType = functionType;
        this.xValues = xValues;
        this.yValues = yValues;
        this.count = count;
        this.userId = userId;
        this.createdAt = createdAt;
        this.isInsertable = isInsertable;
        this.isRemovable = isRemovable;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    @JsonProperty("xValues")
    public double[] getXValues() {
        return xValues;
    }

    @JsonProperty("xValues")
    public void setXValues(double[] xValues) {
        this.xValues = xValues;
    }

    @JsonProperty("yValues")
    public double[] getYValues() {
        return yValues;
    }

    @JsonProperty("yValues")
    public void setYValues(double[] yValues) {
        this.yValues = yValues;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("isInsertable")
    public boolean isInsertable() {
        return isInsertable;
    }

    public void setInsertable(boolean insertable) {
        isInsertable = insertable;
    }

    @JsonProperty("isRemovable")
    public boolean isRemovable() {
        return isRemovable;
    }

    public void setRemovable(boolean removable) {
        isRemovable = removable;
    }
}


