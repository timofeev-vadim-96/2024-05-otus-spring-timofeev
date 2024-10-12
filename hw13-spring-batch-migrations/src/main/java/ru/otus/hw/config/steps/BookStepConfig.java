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
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.relation.Book;

@Configuration
@RequiredArgsConstructor
public class BookStepConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final ModelsMapper modelsMapper;

    @Bean("bookStep")
    public Step bookStep(ItemReader<Book> reader, MongoItemWriter<MongoBook> writer,
                         ItemProcessor<Book, MongoBook> bookProcessor) {
        return new StepBuilder("bookStep", jobRepository)
                .<Book, MongoBook>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .listener(new CommonItemReadListener<Book>())
                .listener(new CommonItemWriteListener<MongoBook>())
                .listener(new CommonItemProcessListener<Book, MongoBook>())
                .listener(new CommonChunkListener())
//                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Book> bookReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(emf)
                .queryString("select b from Book b")
                .pageSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Book, MongoBook> bookProcessor() {
        return modelsMapper::convertBook;
    }

    @Bean
    public MongoItemWriter<MongoBook> bookWriter() {
        MongoItemWriter<MongoBook> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        return writer;
    }
}
