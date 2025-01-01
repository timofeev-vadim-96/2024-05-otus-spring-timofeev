package ru.otus.hw.mongock.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.ReactiveAuthorRepository;
import ru.otus.hw.repositories.ReactiveBookRepository;
import ru.otus.hw.repositories.ReactiveCommentRepository;
import ru.otus.hw.repositories.ReactiveGenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ChangeLog(order = "001")
@Slf4j
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "insertAuthors", author = "wonderWaffle")
    public void insertAuthors(ReactiveAuthorRepository authorRepository) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Author author = new Author();
            author.setFullName("Author_" + i);
            authors.add(author);
        }

        List<Author> saved = authorRepository.saveAll(authors).collectList().block();
        log.info("saved authors: {}", saved);
    }

    @ChangeSet(order = "002", id = "insertGenres", author = "wonderWaffle")
    public void insertGenres(ReactiveGenreRepository genreRepository) {
        List<Genre> genres = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Genre genre = new Genre();
            genre.setName("Genre_" + i);
            genres.add(genre);
        }

        List<Genre> saved = genreRepository.saveAll(genres).collectList().block();
        log.info("saved genres: {}", saved);
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "wonderWaffle")
    public void insertBooks(ReactiveAuthorRepository authorRepository,
                            ReactiveGenreRepository genreRepository,
                            ReactiveBookRepository bookRepository) {
        Mono.zip(authorRepository.findAll().collectList(), genreRepository.findAll().collectList())
                .flatMapMany(tuple -> Flux.range(0, 3)
                        .flatMap(index -> {
                            List<Author> authors = tuple.getT1();
                            List<Genre> genres = tuple.getT2();

                            Book book = new Book();
                            book.setTitle("BookTitle_" + index);
                            book.setAuthor(authors.get(index));
                            book.setGenres(Set.of(genres.get(index), genres.get(genres.size() / 2 + index)));
                            return bookRepository.save(book);
                        })).collectList().block();
    }

    @ChangeSet(order = "004", id = "insertComments", author = "wonderWaffle")
    public void insertComments(ReactiveBookRepository bookRepository, ReactiveCommentRepository commentRepository) {
        bookRepository.findAll().collectList()
                .flatMapMany(books -> Flux.range(0, 3)
                        .flatMap(index -> {
                            Comment comment = new Comment();
                            comment.setText("comment for the " + index + "st book");
                            comment.setBook(books.get(index));

                            return commentRepository.save(comment);
                        })).collectList().block();
    }
}
