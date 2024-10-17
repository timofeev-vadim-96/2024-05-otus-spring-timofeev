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
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.relation.Author;

@Configuration
@RequiredArgsConstructor
public class AuthorStepConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final ModelsMapper modelsMapper;

    @Bean("authorStep")
    public Step authorStep(ItemReader<Author> reader, MongoItemWriter<MongoAuthor> writer,
                           ItemProcessor<Author, MongoAuthor> commentProcessor) {
        return new StepBuilder("authorStep", jobRepository)
                .<Author, MongoAuthor>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .listener(new CommonItemReadListener<Author>())
                .listener(new CommonItemWriteListener<MongoAuthor>())
                .listener(new CommonItemProcessListener<Author, MongoAuthor>())
                .listener(new CommonChunkListener())
                .build();
    }

    @Bean
    public ItemProcessor<Author, MongoAuthor> authorProcessor() {
        return modelsMapper::convertAuthor;
    }

    @Bean
    public MongoItemWriter<MongoAuthor> authorWriter() {
        MongoItemWriter<MongoAuthor> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("authors");
        return writer;
    }

    @Bean
    public JpaPagingItemReader<Author> authorReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(emf)
                .queryString("select a from Author a")
                .pageSize(1000)
                .build();
    }
}
