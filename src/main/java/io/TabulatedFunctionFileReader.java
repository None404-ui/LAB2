package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Класс для чтения табулированных функций из файлов
 */
public class TabulatedFunctionFileReader {
    
    public static void main(String[] args) {
        try (
            BufferedReader arrayReader = new BufferedReader(new FileReader("input/function.txt"));
            BufferedReader linkedListReader = new BufferedReader(new FileReader("input/function.txt"))
        ) {
            // Читаем функцию с фабрикой массива
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(arrayReader, new ArrayTabulatedFunctionFactory());
            System.out.println("Array function:");
            System.out.println(arrayFunction.toString());
            
            // Читаем функцию с фабрикой связного списка
            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(linkedListReader, new LinkedListTabulatedFunctionFactory());
            System.out.println("\nLinked list function:");
            System.out.println(linkedListFunction.toString());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

