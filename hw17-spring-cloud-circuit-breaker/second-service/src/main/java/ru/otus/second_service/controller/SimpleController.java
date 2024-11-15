package ru.otus.second_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class SimpleController {

    @GetMapping("/second-service/api/v1")
    public ResponseEntity<String> getInfo() throws InterruptedException {
        Random random = new Random();
        int delay = random.nextInt(1000, 5001);
        Thread.sleep(delay);
        return new ResponseEntity<>("info from second service", HttpStatus.OK);
    }
}
