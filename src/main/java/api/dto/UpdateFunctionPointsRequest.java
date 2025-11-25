package api.dto;

public class UpdateFunctionPointsRequest {
    private double[] yValues;

    public UpdateFunctionPointsRequest() {}

    public UpdateFunctionPointsRequest(double[] yValues) {
        this.yValues = yValues;
    }

    public double[] getYValues() {
        return yValues;
    }

    public void setYValues(double[] yValues) {
        this.yValues = yValues;
    }
}



