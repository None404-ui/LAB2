package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;
import java.io.*;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        // Часть 1: Чтение из бинарного файла
        try (FileInputStream fileStream = new FileInputStream("input/binary function.bin");
             BufferedInputStream bufferedStream = new BufferedInputStream(fileStream)) {

            // Читаем функцию как ArrayTabulatedFunction
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(
                    bufferedStream, new ArrayTabulatedFunctionFactory());

            System.out.println("Функция из файла:");
            System.out.println(arrayFunction.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении из файла:");
            e.printStackTrace();
        }

        // Часть 2: Чтение из консоли
        System.out.println("Введите размер и значения функции");

        try {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Используем текстовый метод для чтения из консоли
            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(
                    consoleReader, new LinkedListTabulatedFunctionFactory());

            // Вычисляем производную
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = operator.derive(consoleFunction);

            System.out.println("Производная введенной функции:");
            System.out.println(derivative.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении из консоли:");
            e.printStackTrace();
        }
    }
}