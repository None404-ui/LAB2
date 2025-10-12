package io;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import operations.TabulatedDifferentialOperator;

import java.io.*;

/**
 * Класс для демонстрации сериализации и десериализации ArrayTabulatedFunction
 */
public class ArrayTabulatedFunctionSerialization {
    
    public static void main(String[] args) {
        // Сериализация
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream("output/serialized array functions.bin"))) {
            
            // Создаем исходную функцию
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};
            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
            
            // Создаем оператор для вычисления производных
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            
            // Вычисляем первую производную
            TabulatedFunction firstDerivative = operator.derive(function);
            
            // Вычисляем вторую производную
            TabulatedFunction secondDerivative = operator.derive(firstDerivative);
            
            // Сериализуем все три функции
            FunctionsIO.serialize(outputStream, function);
            FunctionsIO.serialize(outputStream, firstDerivative);
            FunctionsIO.serialize(outputStream, secondDerivative);
            
            System.out.println("Функции успешно сериализованы");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Десериализация
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream("output/serialized array functions.bin"))) {
            
            // Десериализуем все три функции
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(inputStream);
            
            // Выводим функции в консоль
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedFunction.toString());
            
            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());
            
            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

