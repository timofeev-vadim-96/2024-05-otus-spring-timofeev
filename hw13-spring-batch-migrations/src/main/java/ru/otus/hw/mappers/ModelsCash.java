package ru.otus.hw.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoGenre;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ModelsCash {
    private static final ConcurrentHashMap<Long, MongoAuthor> authors = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, MongoGenre> genres = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, MongoBook> books = new ConcurrentHashMap<>();

    public void putAuthor(Long id, MongoAuthor author) {
        authors.put(id, author);
    }

    public void putGenre(Long id, MongoGenre genre) {
        genres.put(id, genre);
    }

    public void putBook(Long id, MongoBook book) {
        books.put(id, book);
    }

    public MongoAuthor getAuthor(Long id) {
        return authors.get(id);
    }

    public MongoGenre getGenre(Long id) {
        return genres.get(id);
    }

    public MongoBook getBook(Long id) {
        return books.get(id);
    }
}
