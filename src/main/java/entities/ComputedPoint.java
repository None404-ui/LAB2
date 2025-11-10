package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "computed_points")
@IdClass(ComputedPointId.class)
public class ComputedPoint {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", nullable = false)
    private Function function;

    @Id
    @Column(name = "x_value", nullable = false)
    private Double xValue;

    @Column(name = "y_value", nullable = false)
    private Double yValue;

    public ComputedPoint() {
    }

    public ComputedPoint(Function function, Double xValue, Double yValue) {
        this.function = function;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
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
}



