package functions;

/**
 * Класс для функции, всегда возвращающей 0.
 */
@MathFunctionInfo(name = "Нулевая функция (0)", priority = 4)
public class ZeroFunction extends ConstantFunction {
    public ZeroFunction() {
        super(0);
    }
}