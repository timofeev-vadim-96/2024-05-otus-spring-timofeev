package ru.otus.second_service.service;

import java.util.concurrent.CompletableFuture;

public interface InfoProvider {
    CompletableFuture<String> getAdditionalInfo();
}
