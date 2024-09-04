package ru.otus.hw.security;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.security.web.authentication.AuthenticationFilter;
import ru.otus.hw.models.CustomUser;
import ru.otus.hw.repositories.UserRepository;
import ru.otus.hw.security.filter.PrincipalFilter;
import ru.otus.hw.util.Authority;

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
        return http
                .formLogin(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //возможность работать с проксированными HTTP-запросам
                .rememberMe(rm -> rm.key(rmKey)
                        .tokenValiditySeconds(3600)) //1 hour
                .addFilterAfter(new PrincipalFilter(), AuthorizationFilter.class)
                .authorizeHttpRequests(authz-> authz
                        //book
                        .requestMatchers(HttpMethod.GET, "/edit/{id}").hasRole(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/{id}").hasRole(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/create").hasRole(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/").hasRole(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/delete/{id}").hasRole(Authority.ADMIN.name())

                        //comment
                        .requestMatchers(HttpMethod.POST, "/comment/{bookId}").hasRole(Authority.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/comment/delete/{id}").hasRole(Authority.ADMIN.name())

                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                CustomUser user = userRepository.findByPrincipal(username)
                        .orElseThrow(()->
                                new EntityNotFoundException("user with username: %s not found".formatted(username)));

                return new User(
                        user.getPrincipal(),
                        user.getCredentials(),
                        List.of(new SimpleGrantedAuthority(user.getAuthority())));
            }
        };
    }
}
