package API.dtoForApi;

public class PointRequest {
    private Integer functionId;
    private Double xValue;
    private Double yValue;

    public Integer getFunctionId() { return functionId; }
    public void setFunctionId(Integer functionId) { this.functionId = functionId; }
    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }
    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }
}