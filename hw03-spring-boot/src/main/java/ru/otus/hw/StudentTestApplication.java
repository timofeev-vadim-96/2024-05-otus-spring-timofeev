package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentTestApplication {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        SpringApplication.run(StudentTestApplication.class, args);
    }
}