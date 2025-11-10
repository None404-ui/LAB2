package db.model;


import java.util.Objects;

public class Point {
    private final int pointId;
    private final int functionId;
    private final double xValue;
    private final double yValue;

    public Point(int pointId, int functionId, double xValue, double yValue) {
        this.pointId = pointId;
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    // Getters
    public int getPointId() { return pointId; }
    public int getFunctionId() { return functionId; }
    public double getXValue() { return xValue; }
    public double getYValue() { return yValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return pointId == point.pointId && functionId == point.functionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointId, functionId);
    }

    @Override
    public String toString() {
        return String.format("Point{id=%d, functionId=%d, x=%.2f, y=%.2f}", pointId, functionId, xValue, yValue);
    }
}