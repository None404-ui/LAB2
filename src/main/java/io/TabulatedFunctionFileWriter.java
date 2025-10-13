package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.IOException;

import java.io.*;

/**
 * Класс для записи табулированных функций в файлы
 */
public class TabulatedFunctionFileWriter {
    
    public static void main(String[] args) {
        try (
                BufferedOutputStream arrayWriter = new BufferedOutputStream(new FileOutputStream("output/array function.bin"));
                BufferedOutputStream linkedListWriter = new BufferedOutputStream(new FileOutputStream("output/linked list function.bin"))
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

