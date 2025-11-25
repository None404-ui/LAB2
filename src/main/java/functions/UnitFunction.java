package functions;

/**
 * Класс для функции, всегда возвращающей 1.
 */
@MathFunctionInfo(name = "Единичная функция (1)", priority = 3)
public class UnitFunction extends ConstantFunction {
    public UnitFunction() {
        super(1);
    }
}