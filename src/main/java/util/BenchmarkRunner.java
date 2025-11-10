package util;

import config.DataSourceConfig;
import config.JpaConfig;
import entities.ComputedPoint;
import entities.Function;
import entities.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import repositories.ComputedPointRepository;
import repositories.FunctionRepository;
import repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("🏃 Измерение скорости запросов...");

        try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext()) {

            context.register(DataSourceConfig.class, JpaConfig.class);
            context.refresh();

            UserRepository userRepo = context.getBean(UserRepository.class);
            FunctionRepository functionRepo = context.getBean(FunctionRepository.class);
            ComputedPointRepository pointRepo = context.getBean(ComputedPointRepository.class);

            // Измерение производительности
            runBenchmarks(userRepo, functionRepo, pointRepo);

        } catch (Exception e) {
            System.err.println("❌ Ошибка:");
            e.printStackTrace();
        }
    }

    private static void runBenchmarks(UserRepository userRepo,
                                    FunctionRepository functionRepo,
                                    ComputedPointRepository pointRepo) {

        System.out.println("\n📊 РЕЗУЛЬТАТЫ:");

        // Тест 1: Поиск пользователя
        long userSearchTime = measureUserSearch(userRepo);
        System.out.println("Поиск пользователя: " + userSearchTime + " ms");

        // Тест 2: Получение функций пользователя
        long functionsTime = measureUserFunctions(functionRepo, userRepo);
        System.out.println("Получение функций: " + functionsTime + " ms");

        // Тест 3: Получение точек функции
        long pointsTime = measureFunctionPoints(pointRepo, functionRepo);
        System.out.println("Получение точек: " + pointsTime + " ms");

        // Тест 4: Создание данных
        long createTime = measureCreateOperation(userRepo, functionRepo, pointRepo);
        System.out.println("Создание данных: " + createTime + " ms");

        // Создание таблицы
        createResultsTable(userSearchTime, functionsTime, pointsTime, createTime);
    }

    private static long measureUserSearch(UserRepository userRepo) {
        long totalTime = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            String username = "user" + (i % 1000);

            long start = System.nanoTime();
            Optional<User> user = userRepo.findByUsername(username);
            long end = System.nanoTime();

            if (user.isPresent()) {
                totalTime += (end - start) / 1_000_000;
            }
        }

        return totalTime / iterations;
    }

    private static long measureUserFunctions(FunctionRepository functionRepo, UserRepository userRepo) {
        long totalTime = 0;
        int iterations = 50;

        for (int i = 0; i < iterations; i++) {
            String username = "user" + (i % 1000);
            Optional<User> userOpt = userRepo.findByUsername(username);

            if (userOpt.isPresent()) {
                long start = System.nanoTime();
                List<Function> functions = functionRepo.findByUser(userOpt.get());
                long end = System.nanoTime();

                totalTime += (end - start) / 1_000_000;
            }
        }

        return totalTime / iterations;
    }

    private static long measureFunctionPoints(ComputedPointRepository pointRepo, FunctionRepository functionRepo) {
        long totalTime = 0;
        int iterations = 50;

        List<Function> allFunctions = functionRepo.findAll();
        if (allFunctions.size() < iterations) return 0;

        for (int i = 0; i < iterations; i++) {
            Function function = allFunctions.get(i);

            long start = System.nanoTime();
            List<ComputedPoint> points = pointRepo.findByFunction(function);
            long end = System.nanoTime();

            totalTime += (end - start) / 1_000_000;
        }

        return totalTime / iterations;
    }

    private static long measureCreateOperation(UserRepository userRepo,
                                             FunctionRepository functionRepo,
                                             ComputedPointRepository pointRepo) {
        long totalTime = 0;
        int iterations = 20;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            // Создание пользователя
            User user = new User("test_user_" + i, "test" + i + "@test.com", "pass");
            user = userRepo.save(user);

            // Создание функции
            Function function = new Function("test_func_" + i, user, "x*x");
            function = functionRepo.save(function);

            // Создание точек
            for (int j = 0; j < 10; j++) {
                ComputedPoint point = new ComputedPoint(function, j * 1.0, j * j * 1.0);
                pointRepo.save(point);
            }

            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000;

            // Очистка тестовых данных
            List<ComputedPoint> points = pointRepo.findByFunction(function);
            pointRepo.deleteAll(points);
            functionRepo.delete(function);
            userRepo.delete(user);
        }

        return totalTime / iterations;
    }

    private static void createResultsTable(long userSearch, long functions,
                                         long points, long create) {
        String table = "\n| Операция | Время (ms) |\n" +
                      "|----------|------------|\n" +
                      "| Поиск пользователя | " + userSearch + " |\n" +
                      "| Получение функций пользователя | " + functions + " |\n" +
                      "| Получение точек функции | " + points + " |\n" +
                      "| Создание данных | " + create + " |\n";

        System.out.println(table);

        // Сохранение в файл
        String content = "# Результаты тестирования производительности\n\n" +
                        "## Тестовая среда\n" +
                        "- База данных: PostgreSQL\n" +
                        "- ORM: Spring Data JPA + Hibernate\n" +
                        "- Размер данных: 11,000 записей на таблицу\n\n" +
                        "## Результаты\n" + table + "\n" +
                        "*Время указано в миллисекундах (среднее значение)*";

        try {
            java.nio.file.Files.write(
                java.nio.file.Paths.get("performance_results.md"),
                content.getBytes()
            );
            System.out.println("💾 Таблица сохранена в performance_results.md");
        } catch (Exception e) {
            System.err.println("❌ Ошибка сохранения: " + e.getMessage());
        }
    }
}
