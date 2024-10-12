package ru.otus.hw.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import ru.otus.hw.model.Adventurer;
import ru.otus.hw.service.JourneyService;
import ru.otus.hw.util.Race;

@Configuration
@EnableConfigurationProperties(AppParams.class)
public class IntegrationConfig {
    @Bean("inputChannel")
    public MessageChannel inputChannel() {
        return new QueueChannel(10);
    }

    @Bean("survivalsChannel")
    public MessageChannel survivalsChannel() {
        return MessageChannels.queue(10).getObject();
    }

    @Bean("cemetery")
    public MessageChannel cemeteryChannel() {
        return MessageChannels.queue(10).getObject();
    }

    @Bean
    public IntegrationFlow journey(JourneyService journeyService) {
        return IntegrationFlow.from("inputChannel")
                .split()
                .handle(journeyService, "magicalBomb")
                .<Adventurer, Boolean>route(Adventurer::isAlive, m -> m
                        .channelMapping(true, "survivalsChannel")
                        .channelMapping(false, "cemetery"))
                .get();
    }

    @Bean
    public IntegrationFlow finish() {
        return IntegrationFlow.from("survivalsChannel")
                .<Adventurer>filter(a -> !a.getRace().equals(Race.Undead))
                .<Adventurer, Adventurer>transform(a -> {
                    a.setLevel(a.getLevel() + 1);
                    return a;
                })
                .handle(message -> {
                    Adventurer adventurer = (Adventurer) message.getPayload();
                    System.out.printf("%nCongratulation for %s! You are a survival!%n", adventurer.getFullName());
                })
                .get();
    }

    @Bean
    public IntegrationFlow funeral() {
        return IntegrationFlow.from("cemetery")
                .handle(message -> {
                    Adventurer adventurer = (Adventurer) message.getPayload();
                    System.out.printf("%nR.I.P, %s...%n", adventurer.getFullName());
                })
                .get();
    }
}
