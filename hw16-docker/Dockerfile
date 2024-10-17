FROM alpine:3.20

LABEL maintainer = "Vadim Timofeev"
LABEL telegram = "@w0nder_waffle"

# Установка JDK 17
RUN apk add --no-cache openjdk17-jre

# Копирование JAR-файла в образ с переименованием в app.jar
COPY target/*.jar app.jar

EXPOSE 8080

# Установка точки входа. При запуске приложения будет выполняться `java -jar app.jar`
ENTRYPOINT ["java", "-jar", "app.jar"]