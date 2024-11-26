package ru.otus.second_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.second_service.service.SimpleService;
import ru.otus.second_service.service.dto.SimpleMessage;

@RestController
@RequiredArgsConstructor
public class SimpleController {
    private final SimpleService simpleService;

    @GetMapping("/first-service/api/v1")
    public ResponseEntity<SimpleMessage> getInfo() {
        SimpleMessage info = simpleService.info();
        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}
