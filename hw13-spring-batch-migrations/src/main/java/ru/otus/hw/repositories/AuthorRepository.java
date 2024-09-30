package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.mongo.MongoAuthor;

@Repository
public interface AuthorRepository extends MongoRepository<MongoAuthor, String> {
}
