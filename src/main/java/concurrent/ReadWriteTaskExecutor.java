package concurrent;

import functions.*;
import functions.factory.LinkedListTabulatedFunctionFactory;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {

        // Шаг 1: Создаем табулированную функцию
        ConstantFunction constantFunction = new ConstantFunction(-1); // константа -1
        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(constantFunction, 1, 1000, 1000);

        // Шаг 2: Создаем задачи
        ReadTask readTask = new ReadTask(tabulatedFunction);
        WriteTask writeTask = new WriteTask(tabulatedFunction, 0.5);

        // Шаг 3: Создаем потоки
        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        // Шаг 4: Запускаем потоки
        readThread.start();
        writeThread.start();
    }
}