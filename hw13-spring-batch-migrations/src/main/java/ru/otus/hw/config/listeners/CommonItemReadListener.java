package ru.otus.hw.config.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

public class CommonItemReadListener<T> implements ItemReadListener<T> {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Override
    public void beforeRead() {
        logger.info("Начало чтения");
    }

    @Override
    public void afterRead(T item) {
        logger.info("Конец чтения");
    }

    @Override
    public void onReadError(Exception ex) {
        logger.info("Ошибка чтения");
    }
}
