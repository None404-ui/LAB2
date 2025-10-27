package concurrent;

import functions.TabulatedFunction;

public class ReadTask implements Runnable {
    // приватное поле для хранения табулированной функции
    private final TabulatedFunction function;

    // конструктор принимает табулированную функцию
    public ReadTask(TabulatedFunction function) {
        this.function = function;
    }

    // метод run()
    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            // получаем значения x и y по текущему индексу
            double x = function.getX(i);
            double y = function.getY(i);

            // вывод в консоль
            System.out.printf("After read: i = %d, x = %f, y = %f%n", i, x, y);
        }
    }
}