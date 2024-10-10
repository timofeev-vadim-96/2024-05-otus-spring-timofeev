package ru.otus.hw.config.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class CommonItemProcessListener<T, E> implements ItemProcessListener<T, E> {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Override
    public void beforeProcess(T item) {
        logger.info("Начало обработки");
    }

    @Override
    public void afterProcess(T item, E result) {
        logger.info("Конец обработки");
    }

    @Override
    public void onProcessError(T item, Exception e) {
        logger.info("Ошибка обработки");
    }
}
