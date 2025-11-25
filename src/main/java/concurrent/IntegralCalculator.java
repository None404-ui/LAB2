package concurrent;

import functions.TabulatedFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Параллельный калькулятор определённого интеграла
 */
public class IntegralCalculator {
    
    private static final int MAX_THREADS = 16;
    private static final int STEPS_PER_THREAD = 10000;

    /**
     * Вычисляет определённый интеграл функции на всей области определения
     * @param function табулированная функция
     * @param threadCount количество потоков (1-16)
     * @return значение интеграла
     */
    public static double calculate(TabulatedFunction function, int threadCount) throws Exception {
        if (threadCount < 1) threadCount = 1;
        if (threadCount > MAX_THREADS) threadCount = MAX_THREADS;
        
        int count = function.getCount();
        if (count < 2) {
            throw new IllegalArgumentException("Функция должна содержать минимум 2 точки");
        }
        
        double from = function.getX(0);
        double to = function.getX(count - 1);
        double range = to - from;
        
        if (range <= 0) {
            throw new IllegalArgumentException("Некорректный интервал функции");
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Double>> futures = new ArrayList<>();
        
        double partSize = range / threadCount;
        
        for (int i = 0; i < threadCount; i++) {
            double partFrom = from + i * partSize;
            double partTo = from + (i + 1) * partSize;
            
            IntegralTask task = new IntegralTask(function, partFrom, partTo, STEPS_PER_THREAD);
            futures.add(executor.submit(task));
        }
        
        double result = 0.0;
        for (Future<Double> future : futures) {
            result += future.get();
        }
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        return result;
    }
    
    /**
     * Максимальное количество потоков
     */
    public static int getMaxThreads() {
        return MAX_THREADS;
    }
}



