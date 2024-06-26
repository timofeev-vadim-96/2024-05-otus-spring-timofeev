package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.Availability;
import org.springframework.shell.AvailabilityProvider;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestCommandsServiceImpl;

@Configuration
@Slf4j
public class ShellBeansConfig {

    @Bean
    public AvailabilityProvider showResultCommandAvailability(
            @Qualifier("testCommandsServiceImpl") TestCommandsServiceImpl testRunnerService,
            LocalizedIOService localizedIOService) {

        return () -> {
            TestResult result = testRunnerService.getResult();
            return result != null ? Availability.available() : Availability.unavailable(
                    localizedIOService.getMessage("AvailabilityProvider.showResult.unavailable"));
        };
    }
}
