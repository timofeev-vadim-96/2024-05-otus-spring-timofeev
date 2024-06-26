package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
public class StudentTestApplication {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        SpringApplication.run(StudentTestApplication.class, args);
    }
}