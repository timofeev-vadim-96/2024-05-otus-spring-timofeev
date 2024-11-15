package ru.otus.second_service.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.otus.second_service.service.dto.SimpleMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleService {
    private final InfoClient feignService;

    public SimpleMessage info() {
        SimpleMessage responseMessage = new SimpleMessage();
        responseMessage.setInfoFromFirstService("some info from first service");
        String infoFromSecondService = getAdditionalInfo();
        responseMessage.setInfoFromSecondService(infoFromSecondService);

        return responseMessage;
    }

    private String getAdditionalInfo() {
        try {
            String additionalInfo = feignService.additionalInfo();
            log.info("info from second service:{}", additionalInfo);
            return additionalInfo;
        } catch (CallNotPermittedException e) {
            log.error("can't get additional info because of Circuit Breaker, error:{}", e.getMessage());
            return Strings.EMPTY;
        } catch (RequestNotPermitted e) {
            log.error("can't get additional info because of RateLimiter, error:{}", e.getMessage());
            return Strings.EMPTY;
        } catch (Throwable ex) {
            log.error("can't execute additional info, error:{}", ex.getMessage());
            return Strings.EMPTY;
        }
    }
}
