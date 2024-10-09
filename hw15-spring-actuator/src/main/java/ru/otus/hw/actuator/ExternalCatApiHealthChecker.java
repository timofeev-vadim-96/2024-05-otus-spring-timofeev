package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ExternalCatApiHealthChecker implements HealthIndicator {
    private final RestTemplate restTemplate;

    @Override
    public Health health() {
        String catUrl = "https://http.cat/200";
        ResponseEntity<String> response = restTemplate.exchange(catUrl, HttpMethod.GET, null, String.class);
        if (response.getStatusCode().value() == 200) {
            return Health.up().withDetail("description", "cat api is available and works fine").build();
        } else {
            return Health.down().withDetail("reason", "cat api answered with unexpected way").build();
        }
    }
}
