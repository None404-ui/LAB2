package entities;

import java.io.Serializable;
import java.util.Objects;

public class ComputedPointId implements Serializable {
    private Integer function;
    private Double xValue;

    public ComputedPointId() {
    }

    public ComputedPointId(Integer function, Double xValue) {
        this.function = function;
        this.xValue = xValue;
    }

    public Integer getFunction() {
        return function;
    }

    public void setFunction(Integer function) {
        this.function = function;
    }

    public Double getXValue() {
        return xValue;
    }

    public void setXValue(Double xValue) {
        this.xValue = xValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComputedPointId that = (ComputedPointId) o;
        return Objects.equals(function, that.function) &&
               Objects.equals(xValue, that.xValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, xValue);
    }
}



