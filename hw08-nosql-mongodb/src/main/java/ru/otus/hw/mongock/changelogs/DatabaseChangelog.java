package ru.otus.hw.mongock.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ChangeLog(order = "001")
public class DatabaseChangelog {
    @ChangeSet(order = "001", id = "insertAuthors", author = "wonderWaffle")
    public void insertAuthors(AuthorRepository authorRepository) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Author author = new Author();
            author.setFullName("Author_" + i);
            authors.add(author);
        }

        authorRepository.saveAll(authors);
    }

    @ChangeSet(order = "002", id = "insertGenres", author = "wonderWaffle")
    public void insertGenres(GenreRepository genreRepository) {
        List<Genre> genres = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Genre genre = new Genre();
            genre.setName("Genre_" + i);
            genres.add(genre);
        }

        genreRepository.saveAll(genres);
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "wonderWaffle")
    public void insertBooks(AuthorRepository authorRepository,
                            GenreRepository genreRepository,
                            BookRepository bookRepository) {
        List<Book> books = new ArrayList<>();
        List<Author> authors = authorRepository.findAll();
        List<Genre> genres = genreRepository.findAll();
        for (int i = 0; i < 3; i++) {
            Book book = new Book();
            book.setTitle("BookTitle_" + i);
            book.setAuthor(authors.get(i));
            book.setGenres(Set.of(genres.get(i), genres.get(genres.size() / 2 + i)));

            books.add(book);
        }

        bookRepository.saveAll(books);
    }

    @ChangeSet(order = "004", id = "insertComments", author = "wonderWaffle")
    public void insertComments(BookRepository bookRepository, CommentRepository commentRepository) {
        List<Book> books = bookRepository.findAll();
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Comment comment = new Comment();
            comment.setText("comment for 1st book");
            comment.setBook(books.get(i));

            comments.add(comment);
        }

        commentRepository.saveAll(comments);
    }
}
