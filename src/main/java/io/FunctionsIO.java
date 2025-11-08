package io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * Класс для ввода/вывода табулированных функций
 * Не может иметь наследников и экземпляров
 */
public final class FunctionsIO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionsIO.class);
    
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
        logger.info("Writing function to text stream: {} points", function.getCount());
        PrintWriter printWriter = new PrintWriter(writer);
        
        // Записываем количество точек
        printWriter.println(function.getCount());
        
        // Записываем все точки (x y)
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
            logger.trace("Written point: x={}, y={}", point.x, point.y);
        }
        
        // Сбрасываем буфер, но не закрываем поток
        printWriter.flush();
        logger.debug("Function written to text stream successfully");
    }

    /**
     * Записывает табулированную функцию в байтовый поток
     * @param outputStream буферизованный байтовый поток
     * @param function табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void writeTabulatedFunction(BufferedOutputStream outputStream,
                                              TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        // Записываем количество точек
        dataOutputStream.writeInt(function.getCount());

        // Записываем все точки (x, y)
        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }

        // Сбрасываем буфер, но не закрываем поток
        dataOutputStream.flush();
    }

    /**
     * Читает табулированную функцию из буферизованного символьного потока
     * @param reader буферизованный поток чтения
     * @param factory фабрика для создания функции
     * @return табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {

        logger.info("Reading function from text stream");
        String line = reader.readLine();
        int count = Integer.parseInt(line);
        logger.debug("Reading function with {} points", count);
        
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
                logger.error("Parse error for line: {}", line, e);
                throw new IOException(e);
            }
        }

        logger.debug("Function read from text stream successfully");
        return factory.create(xValues, yValues);
    }

    /**
     * Сериализует табулированную функцию в буферизованный байтовый поток
     * @param stream буферизованный поток записи
     * @param function табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        logger.info("Serializing {} to stream", function.getClass().getSimpleName());

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream)) {
            objectOutputStream.writeObject(function);
            objectOutputStream.flush();
            logger.debug("Serialization completed");
        } catch (IOException e) {
            logger.error("Serialization failed", e);
            throw e;
        }
    }

    /**
     * Десериализует табулированную функцию из буферизованного байтового потока
     * @return табулированная функция
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ClassNotFoundException если класс не найден
     */

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream,
                                                          TabulatedFunctionFactory factory) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        // Читаем количество точек
        int count = dataInputStream.readInt();  // читаем int из потока

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        // читаем double из потока
        for (int i = 0; i < count; i++) {
            xValues[i] = dataInputStream.readDouble();  // читаем x
            yValues[i] = dataInputStream.readDouble();  // читаем y
        }

        return factory.create(xValues, yValues);
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream)
            throws IOException, ClassNotFoundException {
        logger.info("Deserializing function from stream");

        // Создаем ObjectInputStream из BufferedInputStream
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);

        // Читаем объект из потока
        Object obj = objectInputStream.readObject();

        //  Приводим тип к TabulatedFunction
        TabulatedFunction function = (TabulatedFunction) obj;
        //  Возвращаем десериализованную функцию
        logger.debug("Deserialized {} with {} points",
                function.getClass().getSimpleName(), function.getCount());
        return function;
    }

}

