package functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки математических функций, которые должны отображаться
 * в выпадающем списке при создании табулированной функции.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MathFunctionInfo {
    /**
     * Локализованное название функции для отображения в UI
     */
    String name();
    
    /**
     * Приоритет отображения (меньше = выше в списке)
     */
    int priority() default 100;
}



