package ru.otus.second_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.second_service.service.FeignService;

import java.time.Duration;

@Configuration
@Import(FeignClientsConfiguration.class)
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public FeignService clientAdditionalInfoClient(
            Decoder decoder,
            Encoder encoder,
            Contract contract) {

        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .logLevel(Logger.Level.FULL)
                .retryer(new Retryer.Default(500, 5_000, 10)) //параметры повторных ретраев
                .target(FeignService.class,"http"); //http - здесь харкодится URL куда обращаться, но при использовании Eureka не имеет значения что здесь написано. НЕ ПОДСТАВЛЯТЬ КОНКРЕТНЫЙ URL ИЗ ЭВРИКИ, Т.К. ПРИБЬЕМ ГВОЗДЯМИ ИНСТАНС ПРИЛОЖЕНИЯ, К КОТОРОМУ ОБРАЩАЕМСЯ
    }

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(100)) //точность измерения временного окна, относительно времени текущего запроса - limitRefreshPeriod
                .limitForPeriod(1) //сколько запросов можно отправить
                .limitRefreshPeriod(Duration.ofSeconds(10)) //в этот интервал времени
                .build();
    }

    @Bean
    public RateLimiter rateLimiter(RateLimiterConfig config) {
        return RateLimiter.of("defaultRateLimiter", config);
    }

    //фабрика circuitBreaker
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                //настройка, при которой если приложение, к которому мы обращаемся, не отвечает в течении 5 секунд,
                // то изолировать его и более не ждать ответа
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

    //создание дефолтного circuitBreaker
    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerFactory<?,?> circuitBreakerFactory) {
        return circuitBreakerFactory.create("defaultCircuitBreaker");
    }

}
