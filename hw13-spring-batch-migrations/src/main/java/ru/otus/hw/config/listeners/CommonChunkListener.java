package ru.otus.hw.config.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CommonChunkListener implements ChunkListener {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Override
    public void beforeChunk(ChunkContext context) {
        logger.info("Начало пачки");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        logger.info("Конец пачки");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        logger.info("Ошибка пачки");
    }
}
