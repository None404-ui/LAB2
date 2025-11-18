package util;

import Lab2Application;
import config.JpaConfig;
import entities.Function;
import entities.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import repositories.FunctionRepository;
import repositories.UserRepository;
import services.FunctionSearchService;

import java.util.List;
import java.util.Optional;

public class SortingBenchmark {

    public static void main(String[] args) {
        System.out.println("🏃 Тестирование производительности сортировки...");

        try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext()) {

            context.register(Lab2Application.class);
            context.refresh();

            UserRepository userRepo = context.getBean(UserRepository.class);
            FunctionRepository functionRepo = context.getBean(FunctionRepository.class);
            FunctionSearchService searchService = context.getBean(FunctionSearchService.class);

            runSortingBenchmarks(userRepo, functionRepo, searchService);

        } catch (Exception e) {
            System.err.println("❌ Ошибка:");
            e.printStackTrace();
        }
    }

    private static void runSortingBenchmarks(UserRepository userRepo,
                                           FunctionRepository functionRepo,
                                           FunctionSearchService searchService) {

        // Проверяем количество данных
        long userCount = userRepo.count();
        long functionCount = functionRepo.count();
        System.out.println("\n📊 Размер данных:");
        System.out.println("Пользователей: " + userCount);
        System.out.println("Функций: " + functionCount);

        if (functionCount == 0) {
            System.out.println("❌ Нет данных для тестирования! Сначала запустите DataGeneratorRunner");
            return;
        }

        System.out.println("\n📊 РЕЗУЛЬТАТЫ СОРТИРОВКИ:");

        // Тест 1: Сортировка функций по имени (от А до Я)
        long sortByNameAsc = measureSortingByName(searchService, userRepo, Sort.Direction.ASC);
        System.out.println("Сортировка функций по имени (от А до Я): " + sortByNameAsc + " ms");

        // Тест 2: Сортировка функций по имени (от Я до А)
        long sortByNameDesc = measureSortingByName(searchService, userRepo, Sort.Direction.DESC);
        System.out.println("Сортировка функций по имени (от Я до А): " + sortByNameDesc + " ms");

        // Тест 3: Сортировка функций по ID (по возрастанию)
        long sortByIdAsc = measureSortingById(searchService, userRepo, Sort.Direction.ASC);
        System.out.println("Сортировка функций по ID (по возрастанию): " + sortByIdAsc + " ms");

        // Тест 4: Сортировка функций по ID (по убыванию)
        long sortByIdDesc = measureSortingById(searchService, userRepo, Sort.Direction.DESC);
        System.out.println("Сортировка функций по ID (по убыванию): " + sortByIdDesc + " ms");

        // Тест 5: Сортировка точек по X (по возрастанию)
        long sortPointsByXAsc = measureSortingPointsByX(searchService, functionRepo, Sort.Direction.ASC);
        System.out.println("Сортировка точек по X (по возрастанию): " + sortPointsByXAsc + " ms");

        // Тест 6: Сортировка точек по X (по убыванию)
        long sortPointsByXDesc = measureSortingPointsByX(searchService, functionRepo, Sort.Direction.DESC);
        System.out.println("Сортировка точек по X (по убыванию): " + sortPointsByXDesc + " ms");

        // Тест 7: Сортировка точек по Y (по возрастанию)
        long sortPointsByYAsc = measureSortingPointsByY(searchService, functionRepo, Sort.Direction.ASC);
        System.out.println("Сортировка точек по Y (по возрастанию): " + sortPointsByYAsc + " ms");

        // Тест 8: Сортировка точек по Y (по убыванию)
        long sortPointsByYDesc = measureSortingPointsByY(searchService, functionRepo, Sort.Direction.DESC);
        System.out.println("Сортировка точек по Y (по убыванию): " + sortPointsByYDesc + " ms");

        // Создание таблицы результатов
        createResultsTable(sortByNameAsc, sortByNameDesc, sortByIdAsc, sortByIdDesc,
                          sortPointsByXAsc, sortPointsByXDesc, sortPointsByYAsc, sortPointsByYDesc);
    }

    private static long measureSortingByName(FunctionSearchService searchService,
                                           UserRepository userRepo,
                                           Sort.Direction direction) {
        long totalTime = 0;
        int iterations = 20;

        Optional<User> userOpt = userRepo.findById(1); // Первый пользователь
        if (userOpt.isEmpty()) return 0;

        User user = userOpt.get();

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            Sort sort = Sort.by(direction, "name");
            var result = searchService.findMultiple(user, null, null,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE), sort);

            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000;
        }

        return totalTime / iterations;
    }

    private static long measureSortingById(FunctionSearchService searchService,
                                         UserRepository userRepo,
                                         Sort.Direction direction) {
        long totalTime = 0;
        int iterations = 20;

        Optional<User> userOpt = userRepo.findById(1);
        if (userOpt.isEmpty()) return 0;

        User user = userOpt.get();

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            Sort sort = Sort.by(direction, "functionId");
            var result = searchService.findMultiple(user, null, null,
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE), sort);

            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000;
        }

        return totalTime / iterations;
    }

    private static long measureSortingPointsByX(FunctionSearchService searchService,
                                               FunctionRepository functionRepo,
                                               Sort.Direction direction) {
        long totalTime = 0;
        int iterations = 20;

        List<Function> functions = functionRepo.findAll();
        if (functions.isEmpty()) return 0;

        Function function = functions.get(0);

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            Sort sort = Sort.by(direction, "xValue");
            List<?> points = searchService.findPoints(function, sort);

            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000;
        }

        return totalTime / iterations;
    }

    private static long measureSortingPointsByY(FunctionSearchService searchService,
                                               FunctionRepository functionRepo,
                                               Sort.Direction direction) {
        long totalTime = 0;
        int iterations = 20;

        List<Function> functions = functionRepo.findAll();
        if (functions.isEmpty()) return 0;

        Function function = functions.get(0);

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();

            Sort sort = Sort.by(direction, "yValue");
            List<?> points = searchService.findPoints(function, sort);

            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000;
        }

        return totalTime / iterations;
    }

    private static void createResultsTable(long nameAsc, long nameDesc, long idAsc, long idDesc,
                                         long pointsXAsc, long pointsXDesc, long pointsYAsc, long pointsYDesc) {

        String table = "\n| Что сортируем | Как сортируем | Время (ms) |\n" +
                      "|---------------|---------------|------------|\n" +
                      "| Функции по имени | от А до Я | " + nameAsc + " |\n" +
                      "| Функции по имени | от Я до А | " + nameDesc + " |\n" +
                      "| Функции по ID | по возрастанию | " + idAsc + " |\n" +
                      "| Функции по ID | по убыванию | " + idDesc + " |\n" +
                      "| Точки по X | по возрастанию | " + pointsXAsc + " |\n" +
                      "| Точки по X | по убыванию | " + pointsXDesc + " |\n" +
                      "| Точки по Y | по возрастанию | " + pointsYAsc + " |\n" +
                      "| Точки по Y | по убыванию | " + pointsYDesc + " |\n";

        System.out.println(table);

        // Сохранение в файл
        String content = "# Результаты тестирования сортировки\n\n" +
                        "## Тестовая среда\n" +
                        "- База данных: PostgreSQL\n" +
                        "- ORM: Spring Data JPA + Hibernate\n" +
                        "- Размер данных: 11,000 записей на таблицу\n\n" +
                        "## Результаты\n\n" + table + "\n\n" +
                        "**Примечание:** Время указано в миллисекундах (среднее по 20 запускам)\n\n" +
                        "### Что значит сортировка:\n" +
                        "- **от А до Я** - алфавитный порядок (func0, func1, func10...)\n" +
                        "- **от Я до А** - обратный алфавитный порядок\n" +
                        "- **по возрастанию** - от меньшего к большему (1, 2, 3...)\n" +
                        "- **по убыванию** - от большего к меньшему (999, 998, 997...)";

        try {
            java.nio.file.Files.write(
                java.nio.file.Paths.get("sorting_benchmark_results.md"),
                content.getBytes()
            );
            System.out.println("💾 Результаты сохранены в sorting_benchmark_results.md");
        } catch (Exception e) {
            System.err.println("❌ Ошибка сохранения: " + e.getMessage());
        }
    }
}
