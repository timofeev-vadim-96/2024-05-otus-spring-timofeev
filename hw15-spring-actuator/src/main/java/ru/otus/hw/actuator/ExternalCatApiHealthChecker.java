package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.CatApiService;

@Component
@RequiredArgsConstructor
public class ExternalCatApiHealthChecker implements HealthIndicator {
    private final CatApiService catApiService;

    @Override
    public Health health() {
        boolean isConnectionExists = catApiService.checkApiConnection(200);
        if (isConnectionExists) {
            return Health.up().withDetail("description", "cat api is available and works fine").build();
        } else {
            return Health.down().withDetail("reason", "cat api answered with unexpected way").build();
        }
    }
}
