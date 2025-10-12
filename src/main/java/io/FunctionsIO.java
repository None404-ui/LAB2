package io;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    /**
     * Читает табулированную функцию из буферизованного символьного потока
     * @param reader буферизованный поток чтения
     * @param factory фабрика для создания функции
     * @return табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        String line = reader.readLine();
        int count = Integer.parseInt(line);
        
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));
        
        for (int i = 0; i < count; i++) {
            line = reader.readLine();
            String[] values = line.split(" ");
            
            try {
                xValues[i] = numberFormat.parse(values[0]).doubleValue();
                yValues[i] = numberFormat.parse(values[1]).doubleValue();
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
        
        return factory.create(xValues, yValues);
    }

    /**
     * Сериализует табулированную функцию в буферизованный байтовый поток
     * @param stream буферизованный поток записи
     * @param function табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
        objectOutputStream.writeObject(function);
        objectOutputStream.flush();
    }

    /**
     * Десериализует табулированную функцию из буферизованного байтового потока
     * @param stream буферизованный поток чтения
     * @return табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ClassNotFoundException если класс не найден
     */
    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        return (TabulatedFunction) objectInputStream.readObject();
    }
}

