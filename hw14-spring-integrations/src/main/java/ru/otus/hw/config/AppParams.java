package ru.otus.hw.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "application")
@Getter
public class AppParams {
    private final int teamSize;

    @ConstructorBinding
    public AppParams(int teamSize) {
        this.teamSize = teamSize;
    }
}
