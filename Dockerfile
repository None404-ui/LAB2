# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем приложение (skip тесты для ускорения)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
FROM tomcat:9.0-jdk21-openjdk-slim
WORKDIR /usr/local/tomcat

# Удаляем дефолтные приложения Tomcat
RUN rm -rf webapps/*

# Копируем WAR файл из build stage
COPY --from=build /app/target/*.war webapps/ROOT.war

# Открываем порт 8080
EXPOSE 8080

# Запуск Tomcat
CMD ["catalina.sh", "run"]
