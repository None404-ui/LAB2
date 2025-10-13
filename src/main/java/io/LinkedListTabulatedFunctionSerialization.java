package io;

import functions.*;
import functions.factory.LinkedListTabulatedFunctionFactory;
import java.io.*;
import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import operations.TabulatedDifferentialOperator;


public class LinkedListTabulatedFunctionSerialization {

    public static void main(String[] args) {
        // Создаем исходную функцию
        double[] xValues = {0, 1, 2, 3, 4};
        double[] yValues = {0, 1, 4, 9, 16};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Создаем оператор для производных
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(
                new LinkedListTabulatedFunctionFactory());

        // Вычисляем производные
        TabulatedFunction firstDerivative = operator.derive(function);
        TabulatedFunction secondDerivative = operator.derive(firstDerivative);

        // СЕРИАЛИЗАЦИЯ - запись в файл
        try (BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream("output/serialized linked list functions.bin"))) {

            FunctionsIO.serialize(out, function);
            FunctionsIO.serialize(out, firstDerivative);
            FunctionsIO.serialize(out, secondDerivative);

            System.out.println("Функции успешно сериализованы в файл");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // ДЕСЕРИАЛИЗАЦИЯ - чтение из файла
        try (BufferedInputStream in = new BufferedInputStream(
                new FileInputStream("output/serialized linked list functions.bin"))) {

            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(in);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(in);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(in);

            System.out.println("\nДесериализованные функции:");
            System.out.println("Исходная: " + deserializedFunction);
            System.out.println("Первая производная: " + deserializedFirstDerivative);
            System.out.println("Вторая производная: " + deserializedSecondDerivative);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}