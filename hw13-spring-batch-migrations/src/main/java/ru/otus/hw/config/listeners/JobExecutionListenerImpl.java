package ru.otus.hw.config.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobExecutionListenerImpl implements JobExecutionListener {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Начало job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Конец job");
    }
}
