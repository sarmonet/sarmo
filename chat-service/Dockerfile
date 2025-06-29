# Используем официальный образ OpenJDK для сборки и выполнения приложения
FROM openjdk:21-jdk-slim as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR файл в контейнер
COPY build/libs/chat-service-0.0.1.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
