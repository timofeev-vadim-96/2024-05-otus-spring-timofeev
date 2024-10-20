package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.model.Adventurer;

import java.util.Collection;

@MessagingGateway
public interface JourneyGateway {
    @Gateway(requestChannel = "inputChannel")
    void startJourney(Collection<Adventurer> adventurers);
}
