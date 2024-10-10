package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import ru.otus.hw.config.listeners.JobExecutionListenerImpl;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    public static final String IMPORT_JOB_NAME = "importJob";

    private final JobRepository jobRepository;

    @Bean("importJob")
    public Job importJob(@Qualifier("bookStep") Step bookStep,
                         @Qualifier("commentStep") Step commentStep,
                         @Qualifier("splitFlow") Flow splitFlow) {
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(splitFlow)
                .next(bookStep)
                .next(commentStep)
                .build()
                .listener(new JobExecutionListenerImpl())
                .build();
    }

    @Bean
    public Flow splitFlow(@Qualifier("authorsFlow") Flow authorsFlow,
                          @Qualifier("genresFlow") Flow genresFlow) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor())
                .add(authorsFlow, genresFlow)
                .build();
    }

    @Bean("authorsFlow")
    public Flow authorsFlow(@Qualifier("authorStep") Step authorStep) {
        return new FlowBuilder<SimpleFlow>("authorsFlow")
                .start(authorStep)
                .build();
    }

    @Bean("genresFlow")
    public Flow genresFlow(@Qualifier("genreStep") Step genreStep) {
        return new FlowBuilder<SimpleFlow>("genresFlow")
                .start(genreStep)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("springBatch");
    }
}
