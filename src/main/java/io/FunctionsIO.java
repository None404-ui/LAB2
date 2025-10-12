package io;

import functions.Point;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Класс для ввода/вывода табулированных функций
 * Не может иметь наследников и экземпляров
 */
public final class FunctionsIO {
    
    /**
     * Приватный конструктор, запрещающий создание экземпляров
     * @throws UnsupportedOperationException всегда
     */
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    /**
     * Записывает табулированную функцию в буферизованный символьный поток
     * @param writer буферизованный поток записи
     * @param function табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);
        
        printWriter.println(function.getCount());
        
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
        }
        
        printWriter.flush();
    }
}

