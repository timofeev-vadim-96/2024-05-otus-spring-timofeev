package ru.otus.hw.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "users")
@Getter
public class UserConfig {
    private final String userLogin;

    private final String userPassword;

    private final String adminLogin;

    private final String adminPassword;

    @ConstructorBinding
    public UserConfig(String userLogin, String userPassword, String adminLogin, String adminPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }
}
