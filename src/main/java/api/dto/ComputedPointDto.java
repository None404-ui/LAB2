package api.dto;

import java.time.LocalDateTime;

public class ComputedPointDto {
    private Integer functionId;
    private Double xValue;
    private Double yValue;
    private LocalDateTime computedAt;

    public ComputedPointDto() {}

    public ComputedPointDto(Integer functionId, Double xValue, Double yValue, LocalDateTime computedAt) {
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
        this.computedAt = computedAt;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public Double getXValue() {
        return xValue;
    }

    public void setXValue(Double xValue) {
        this.xValue = xValue;
    }

    public Double getYValue() {
        return yValue;
    }

    public void setYValue(Double yValue) {
        this.yValue = yValue;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }
}


