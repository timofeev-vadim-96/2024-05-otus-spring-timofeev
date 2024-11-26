package ru.otus.second_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "second-service") //value - имя сервиса в Eureka для поиска инстанса
public interface InfoClient {
    @GetMapping(value = "/second-service/api/v1", consumes = "application/json")
    String additionalInfo();
}
