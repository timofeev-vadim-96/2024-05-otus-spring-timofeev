package ru.otus.hw.security;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.filter.PrincipalFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String rmKey;

    public SecurityConfig(@Value("${config.rmKey}") String rmKey) {
        this.rmKey = rmKey;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        secureEndpoints(http);

        return http
                .formLogin(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //возможность работать с проксированными HTTP-запросам
                .rememberMe(rm -> rm.key(rmKey)
                        .tokenValiditySeconds(3600)) //1 hour
                .addFilterAfter(new PrincipalFilter(), AuthorizationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                CustomUser user = userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new EntityNotFoundException("user with username: %s not found".formatted(username)));

                return new User(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority(user.getAuthority())));
            }
        };
    }

    private void secureEndpoints(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.GET, "/error", "/login").permitAll()

                        //book
                        .requestMatchers(HttpMethod.GET, "/edit/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/delete/{id}").hasRole("ADMIN")

                        //comment
                        .requestMatchers(HttpMethod.POST, "/comment/{bookId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/comment/delete/{id}").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );
    }
}
