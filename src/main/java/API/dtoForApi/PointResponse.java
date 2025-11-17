package API.dtoForApi;

import java.time.LocalDateTime;

public class PointResponse {
    private Integer pointId;
    private Integer functionId;
    private Double xValue;
    private Double yValue;
    private LocalDateTime computedAt;

    public Integer getPointId() { return pointId; }
    public void setPointId(Integer pointId) { this.pointId = pointId; }
    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }
    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }
    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }
    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}