package functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для композиции функций.
 */
public class CompositeFunction implements MathFunction {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunction.class);

    private final MathFunction firstFunction;
    private final MathFunction secondFunction;

    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        // ERROR проверка на null-аргументы - критическая ошибка
        if (firstFunction == null || secondFunction == null) {
            logger.error("Попытка создания CompositeFunction с null-функциями: first={}, second={}",
                    firstFunction, secondFunction);
            throw new IllegalArgumentException("Functions cannot be null");
        }

        // INFO создание важного объекта - композитной функции
        logger.info("Создание композитной функции: {} -> {}",
                firstFunction.getClass().getSimpleName(),
                secondFunction.getClass().getSimpleName());

        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    @Override
    public double apply(double x) {
        logger.trace("Applying composite function at x = {}", x);

        // Сначала применяем первую функцию, затем ко результату - вторую
        double intermediate = firstFunction.apply(x);
        double result = secondFunction.apply(intermediate);

        logger.trace("Composite function result: {} -> {} -> {}", x, intermediate, result);
        return result;
    }

    public MathFunction getFirstFunction() {
        return firstFunction;
    }

    public MathFunction getSecondFunction() {
        return secondFunction;
    }
}