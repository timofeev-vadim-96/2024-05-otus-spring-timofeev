package ru.otus.second_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    @GetMapping("/second-service/api/v1")
    public ResponseEntity<String> getInfo() throws InterruptedException {
        Thread.sleep(1_000);
        return new ResponseEntity<>("info from second service", HttpStatus.OK);
    }
}
