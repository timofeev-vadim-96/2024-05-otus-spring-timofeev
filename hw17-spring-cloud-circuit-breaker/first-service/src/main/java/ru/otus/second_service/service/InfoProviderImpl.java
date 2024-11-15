package ru.otus.second_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoProviderImpl implements InfoProvider {
    private final InfoClient infoClient;

    @CircuitBreaker(name = "defaultCircuitBreaker")
    @RateLimiter(name = "defaultRateLimiter")
    @TimeLimiter(name = "defaultTimeLimiter")
    @Retry(name = "defaultRetry")
    public CompletableFuture<String> getAdditionalInfo() {
        return CompletableFuture.supplyAsync(() -> {
        String additionalInfo = infoClient.additionalInfo();
        log.info("info from second service:{}", additionalInfo);
        return additionalInfo;
        });
    }
}
