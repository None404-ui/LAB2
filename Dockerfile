# ---- Stage 1: Build Frontend ----
FROM node:20-alpine AS frontend-build
WORKDIR /frontend

# Копируем frontend
COPY src/main/webapp/package*.json ./
RUN npm install

COPY src/main/webapp/ ./
RUN npm run build

# ---- Stage 2: Build Backend ----
FROM maven:3.9.0-eclipse-temurin-17 AS backend-build
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Копируем собранный frontend в static resources
COPY --from=frontend-build /frontend/dist ./src/main/resources/static

# Собираем приложение (skip тесты для ускорения)
RUN mvn clean package -DskipTests

# ---- Stage 3: Run ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копируем готовый jar из build stage
COPY --from=backend-build /app/target/*.jar app.jar

# Expose порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
