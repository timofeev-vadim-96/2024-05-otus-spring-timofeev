package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.mongo.MongoGenre;

@Repository
public interface GenreRepository extends MongoRepository<MongoGenre, String> {
}
