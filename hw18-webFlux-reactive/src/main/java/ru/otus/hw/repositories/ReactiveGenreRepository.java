package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

import java.util.Set;

@Repository
public interface ReactiveGenreRepository extends ReactiveMongoRepository<Genre, String> {
    @Query("{ 'id': { $in: :#{#ids} } }")
    Flux<Genre> findAllByIds(@Param("ids") Set<String> ids);
}
