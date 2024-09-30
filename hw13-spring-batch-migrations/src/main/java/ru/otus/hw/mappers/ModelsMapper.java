package ru.otus.hw.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.models.relation.Author;
import ru.otus.hw.models.relation.Book;
import ru.otus.hw.models.relation.Comment;
import ru.otus.hw.models.relation.Genre;
import ru.otus.hw.repositories.ModelsCash;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModelsMapper {
    private final ModelsCash cash;

    public MongoBook convertBook(Book book) {
        String uuid = UUID.randomUUID().toString();

        Set<MongoGenre> mongoGenres = new HashSet<>();
        for (Genre genre: book.getGenres()) {
            MongoGenre mongoGenre = cash.getGenre(genre.getId());
            mongoGenres.add(mongoGenre);
        }

        MongoAuthor mongoAuthor = cash.getAuthor(book.getAuthor().getId());

        MongoBook mongoBook = MongoBook.builder()
                .id(uuid)
                .title(book.getTitle())
                .author(mongoAuthor)
                .genres(mongoGenres)
                .build();

        cash.putBook(book.getId(), mongoBook);

        return mongoBook;
    }

    public MongoAuthor convertAuthor(Author author) {
        String uuid = UUID.randomUUID().toString();
        MongoAuthor mongoAuthor = MongoAuthor.builder()
                .id(uuid)
                .fullName(author.getFullName())
                .build();

        cash.putAuthor(author.getId(), mongoAuthor);

        return mongoAuthor;
    }

    public MongoGenre convertGenre(Genre genre) {
        String uuid = UUID.randomUUID().toString();
        MongoGenre mongoGenre = MongoGenre.builder()
                .id(uuid)
                .name(genre.getName())
                .build();

        cash.putGenre(genre.getId(), mongoGenre);

        return mongoGenre;
    }

    public MongoComment convertComment(Comment comment) {
        String uuid = UUID.randomUUID().toString();

        return MongoComment.builder()
                .id(uuid)
                .text(comment.getText())
                .book(cash.getBook(comment.getBook().getId()))
                .build();
    }
}
