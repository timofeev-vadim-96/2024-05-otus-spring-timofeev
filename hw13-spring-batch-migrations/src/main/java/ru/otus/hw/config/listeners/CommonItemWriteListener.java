package ru.otus.hw.config.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

public class CommonItemWriteListener<T> implements ItemWriteListener<T> {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Override
    public void beforeWrite(Chunk<? extends T> items) {
        logger.info("Начало записи");
    }

    @Override
    public void afterWrite(Chunk<? extends T> items) {
        logger.info("Конец записи");
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends T> items) {
        logger.info("Ошибка записи");
    }
}
