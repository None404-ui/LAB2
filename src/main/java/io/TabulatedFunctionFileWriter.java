package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс для записи табулированных функций в файлы
 */
public class TabulatedFunctionFileWriter {
    
    public static void main(String[] args) {
        try (
            BufferedWriter arrayWriter = new BufferedWriter(new FileWriter("output/array function.txt"));
            BufferedWriter linkedListWriter = new BufferedWriter(new FileWriter("output/linked list function.txt"))
        ) {
            // Создаем функцию на основе массива
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};
            TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
            
            // Создаем функцию на основе связного списка
            double[] xValues2 = {0.0, 0.5, 1.0};
            double[] yValues2 = {0.0, 0.25, 1.0};
            TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues2, yValues2);
            
            // Записываем функции в файлы
            FunctionsIO.writeTabulatedFunction(arrayWriter, arrayFunction);
            FunctionsIO.writeTabulatedFunction(linkedListWriter, linkedListFunction);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

