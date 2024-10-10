package ru.otus.hw.config.steps;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.config.listeners.CommonChunkListener;
import ru.otus.hw.config.listeners.CommonItemProcessListener;
import ru.otus.hw.config.listeners.CommonItemReadListener;
import ru.otus.hw.config.listeners.CommonItemWriteListener;
import ru.otus.hw.mappers.ModelsMapper;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.models.relation.Genre;

@Configuration
@RequiredArgsConstructor
public class GenreStepConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final ModelsMapper modelsMapper;

    @Bean("genreStep")
    public Step genreStep(ItemReader<Genre> reader, MongoItemWriter<MongoGenre> writer,
                          ItemProcessor<Genre, MongoGenre> genreProcessor) {
        return new StepBuilder("genreStep", jobRepository)
                .<Genre, MongoGenre>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(genreProcessor)
                .writer(writer)
                .listener(new CommonItemReadListener<Genre>())
                .listener(new CommonItemWriteListener<MongoGenre>())
                .listener(new CommonItemProcessListener<Genre, MongoGenre>())
                .listener(new CommonChunkListener())
                .build();
    }

    @Bean
    public ItemProcessor<Genre, MongoGenre> genreProcessor() {
        return modelsMapper::convertGenre;
    }

    @Bean
    public MongoItemWriter<MongoGenre> genreWriter() {
        MongoItemWriter<MongoGenre> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("genres");
        return writer;
    }

    @Bean
    public JpaPagingItemReader<Genre> genreReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(emf)
                .queryString("select g from Genre g")
                .pageSize(1000)
                .build();
    }
}
