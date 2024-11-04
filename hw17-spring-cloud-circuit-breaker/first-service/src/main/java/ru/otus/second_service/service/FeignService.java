package ru.otus.second_service.service;

import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

public interface FeignService {
    @GetMapping(value ="/second-service/api/v1/", consumes = "application/json")
    String additionalInfo(URI baseUri);
}
