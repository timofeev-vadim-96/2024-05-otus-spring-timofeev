package ru.otus.second_service.service;

import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.functions.CheckedFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.second_service.service.dto.SimpleMessage;

import java.net.URI;
import java.util.UUID;


@Service
@Slf4j
public class SimpleService {
    private final FeignService feignService;

    private final EurekaClient discoveryClient;

    private final CheckedFunction<String, String> getInfoFromSecondServiceFunction;

    public SimpleService(FeignService feignService, EurekaClient discoveryClient, CircuitBreaker circuitBreaker,
                         RateLimiter rateLimiter) {
        this.feignService = feignService;
        this.discoveryClient = discoveryClient;

        this.getInfoFromSecondServiceFunction = RateLimiter.decorateCheckedFunction(rateLimiter,
                name -> circuitBreaker.run(this::getAdditionalInfo,
                        t -> {
                            log.error("the Circuit Breaker has been triggered for the maximum waiting time for a " +
                                    "response from an external service:{}", t.getMessage());
                            return "there are no info from second service because of delay";
                        }));
    }

    //curl -v http://localhost:8081/

    public SimpleMessage info() {
        SimpleMessage responseMessage = new SimpleMessage();
        responseMessage.setInfoFromFirstService("some info from first service");
        try {
            String infoFromSecondService = getInfoFromSecondServiceFunction.apply(UUID.randomUUID().toString());
            responseMessage.setInfoFromSecondService(infoFromSecondService);
        } catch (Throwable ex) {
            log.error("can't execute additional info, error:{}", ex.getMessage());
        }
        return responseMessage;
    }

    private String getAdditionalInfo() {
        try {
            var clientInfo = discoveryClient.getNextServerFromEureka("SECOND-SERVICE", false);
            log.info("clientInfo from Eureka:{}", clientInfo);
            String additionalInfo = feignService.additionalInfo(new URI(clientInfo.getHomePageUrl()));
            log.info("info from second service:{}", additionalInfo);
            return additionalInfo;
        } catch (Exception e) {
            log.error("can't get additional info, error:{}", e.getMessage());
            return "there are no info from second service because of rate limiter";
        }
    }
}
