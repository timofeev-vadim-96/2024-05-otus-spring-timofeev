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
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.relation.Comment;

@Configuration
@RequiredArgsConstructor
public class CommentStepConfig {
    private static final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final ModelsMapper modelsMapper;

    @Bean("commentStep")
    public Step commentStep(ItemReader<Comment> reader, MongoItemWriter<MongoComment> writer,
                            ItemProcessor<Comment, MongoComment> commentProcessor) {
        return new StepBuilder("commentStep", jobRepository)
                .<Comment, MongoComment>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .listener(new CommonItemReadListener<Comment>())
                .listener(new CommonItemWriteListener<MongoComment>())
                .listener(new CommonItemProcessListener<Comment, MongoComment>())
                .listener(new CommonChunkListener())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Comment> commentReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("commentReader")
                .entityManagerFactory(emf)
                .queryString("select c from Comment c")
                .pageSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Comment, MongoComment> commentProcessor() {
        return modelsMapper::convertComment;
    }

    @Bean
    public MongoItemWriter<MongoComment> commentWriter() {
        MongoItemWriter<MongoComment> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("comments");
        return writer;
    }
}
