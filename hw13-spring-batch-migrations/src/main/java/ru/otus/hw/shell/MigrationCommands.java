package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.command.annotation.Command;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

import static ru.otus.hw.config.JobConfig.IMPORT_JOB_NAME;

@Service
@RequiredArgsConstructor
@Command(group = "app-commands")
@Slf4j
public class MigrationCommands {
    private final JobOperator jobOperator;

    private final JobExplorer jobExplorer;

    @Command(command = "showInfo", alias = "inf")
    public void showInfo() {
        log.info(jobExplorer.getJobNames().toString());
        log.info(Objects.requireNonNull(jobExplorer.getLastJobInstance(IMPORT_JOB_NAME)).toString());
    }

    //может быть запущена повторно за счет уникального параметра текущего времени
    @Command(command = "startMigration", alias = "st")
    public void startMigration() throws Exception {
        Properties properties = new Properties();
        properties.put("time", LocalDateTime.now().toString());

        Long executedJobId = jobOperator.start(IMPORT_JOB_NAME, properties);

        log.info(jobOperator.getSummary(executedJobId));
    }
}
