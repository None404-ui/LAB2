package util;

import Lab2Application;
import config.JpaConfig;
import entities.ComputedPoint;
import entities.Function;
import entities.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import repositories.ComputedPointRepository;
import repositories.FunctionRepository;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGeneratorRunner {

    public static void main(String[] args) {
        System.out.println("🔧 Генерация 11k записей на каждую таблицу...");

        try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext()) {

            context.register(Lab2Application.class);
            context.refresh();

            UserRepository userRepo = context.getBean(UserRepository.class);
            FunctionRepository functionRepo = context.getBean(FunctionRepository.class);
            ComputedPointRepository pointRepo = context.getBean(ComputedPointRepository.class);

            // Очистка данных
            System.out.println("🧹 Очистка старых данных...");
            pointRepo.deleteAll();
            functionRepo.deleteAll();
            userRepo.deleteAll();

            // Генерация данных
            generateData(userRepo, functionRepo, pointRepo);

            System.out.println("✅ Данные сгенерированы!");

        } catch (Exception e) {
            System.err.println("❌ Ошибка:");
            e.printStackTrace();
        }
    }

    private static void generateData(UserRepository userRepo,
                                   FunctionRepository functionRepo,
                                   ComputedPointRepository pointRepo) {
        Random random = new Random();

        // 11k пользователей
        System.out.println("👥 Создание пользователей...");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 11000; i++) {
            users.add(new User("user" + i, "user" + i + "@test.com", "pass"));
        }
        userRepo.saveAll(users);

        // 11k функций для первого пользователя
        System.out.println("📈 Создание функций...");
        User firstUser = users.get(0);
        List<Function> functions = new ArrayList<>();
        String[] expressions = {"Math.sin(x)", "Math.cos(x)", "x*x", "Math.sqrt(x)"};

        for (int i = 0; i < 11000; i++) {
            String name = "func" + i;
            String expression = expressions[random.nextInt(expressions.length)];
            functions.add(new Function(name, firstUser, expression));
        }
        functionRepo.saveAll(functions);

        // 11k точек
        System.out.println("📍 Создание точек...");
        List<ComputedPoint> points = new ArrayList<>();
        for (int i = 0; i < 11000; i++) {
            Function func = functions.get(random.nextInt(functions.size()));
            double x = (random.nextDouble() - 0.5) * 10;
            double y = x * x; // простая функция для примера
            points.add(new ComputedPoint(func, x, y));
        }
        pointRepo.saveAll(points);
    }
}
