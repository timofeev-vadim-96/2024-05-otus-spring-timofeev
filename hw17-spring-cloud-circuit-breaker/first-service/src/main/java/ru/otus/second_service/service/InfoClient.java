package ru.otus.second_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@Retry(name = "defaultRetry")
@CircuitBreaker(name = "defaultCircuitBreaker")
@RateLimiter(name = "defaultRateLimiter")
@FeignClient(value = "second-service") //value - имя сервиса в Eureka для поиска инстанса
@TimeLimiter(name = "defaultTimeLimiter")
public interface InfoClient {
    @GetMapping(value = "/second-service/api/v1", consumes = "application/json")
    String additionalInfo();
}
