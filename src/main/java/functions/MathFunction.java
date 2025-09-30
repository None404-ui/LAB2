package functions;

public interface MathFunction {
    double apply(double x);
    default CompositeFunction andThen(MathFunction afterFunction) {
        if (afterFunction == null) {
            throw new NullPointerException("After function cannot be null");
        }
        return new CompositeFunction(this, afterFunction);
    }
}