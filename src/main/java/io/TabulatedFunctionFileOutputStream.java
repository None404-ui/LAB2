package io;

import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.io.*;

/**
 * Класс для записи табулированных функций в файлы
 */
public class TabulatedFunctionFileOutputStream {

    public static void main(String[] args) {
        // Создаем тестовые данные для функций
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0}; // f(x) = x²

        try (
                // Создаем потоки для записи в файлы
                FileOutputStream arrayFileStream = new FileOutputStream("output/array function.bin");
                FileOutputStream linkedFileStream = new FileOutputStream("output/linked list function.bin");

                // Обертываем в буферизованные потоки
                BufferedOutputStream arrayBufferedStream = new BufferedOutputStream(arrayFileStream);
                BufferedOutputStream linkedBufferedStream = new BufferedOutputStream(linkedFileStream)
        ) {
            // Создаем функции
            TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction linkedFunction = new LinkedListTabulatedFunction(xValues, yValues);

            // Записываем функции в соответствующие файлы
            FunctionsIO.writeTabulatedFunction(arrayBufferedStream, arrayFunction);
            FunctionsIO.writeTabulatedFunction(linkedBufferedStream, linkedFunction);

            System.out.println("Функции успешно записаны в файлы:");
            System.out.println("- output/array function.bin");
            System.out.println("- output/linked list function.bin");

        } catch (IOException e) {
            // Обрабатываем исключение
            System.err.println("Ошибка при записи в файл:");
            e.printStackTrace();
        }
    }
}