package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
public interface GenreRepository extends MongoRepository<Genre, String> {
    @Query("{ 'id': { $in: :#{#ids} } }")
    List<Genre> findAllByIds(@Param("ids") Set<String> ids);
}
