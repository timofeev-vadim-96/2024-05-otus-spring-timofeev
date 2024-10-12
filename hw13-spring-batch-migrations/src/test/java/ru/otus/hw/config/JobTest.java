package ru.otus.hw.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.otus.hw.config.JobConfig.IMPORT_JOB_NAME;

@SpringBootTest
@SpringBatchTest
public class JobTest {
    //IDE показывает что не заинжектится, но по факту все норм
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    //IDE показывает что не заинжектится, но по факту все норм
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        assertThatDataBaseIsEmpty();

        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_JOB_NAME);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // Проверка статуса работы
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        assertThatDataBaseIsNotEmptyAndFieldsAreNotNull();
    }

    private void assertThatDataBaseIsEmpty() {
        List<MongoAuthor> authors = authorRepository.findAll();
        List<MongoGenre> genres = genreRepository.findAll();
        List<MongoBook> books = bookRepository.findAll();
        List<MongoComment> comments = commentRepository.findAll();

        assertTrue(authors.isEmpty() &&
                genres.isEmpty() &&
                books.isEmpty() &&
                comments.isEmpty());
    }

    private void assertThatDataBaseIsNotEmptyAndFieldsAreNotNull() {
        List<MongoAuthor> authors = authorRepository.findAll();
        List<MongoGenre> genres = genreRepository.findAll();
        List<MongoBook> books = bookRepository.findAll();
        List<MongoComment> comments = commentRepository.findAll();

        assertThat(authors).isNotEmpty();
        assertThat(genres).isNotEmpty();
        assertThat(books).isNotEmpty();
        assertThat(comments).isNotEmpty();

        assertThatAuthorsAreNotEmpty(authors);

        assertThatGenresAreNotEmpty(genres);

        assertThatBooksAreNotEmptyAndFieldsAreNotNull(books, authors);

        assertThatCommentsAreNotEmptyAndFieldsAreNotNull(comments, books);
    }

    private void assertThatCommentsAreNotEmptyAndFieldsAreNotNull(List<MongoComment> comments, List<MongoBook> books) {
        assertThat(comments).isNotEmpty()
                .allMatch(comment -> {
                    assertThat(comment.getId()).isNotNull();
                    assertThat(comment.getText()).isNotNull();
                    assertThat(books).contains(comment.getBook()); // Проверка принадлежности книги
                    return true;
                });
    }

    private void assertThatBooksAreNotEmptyAndFieldsAreNotNull(List<MongoBook> books, List<MongoAuthor> authors) {
        assertThat(books).isNotEmpty()
                .allMatch(book -> {
                    assertThat(book.getId()).isNotNull();
                    assertThat(book.getTitle()).isNotNull();
                    assertThat(authors).contains(book.getAuthor()); // Проверка принадлежности автора
                    assertThat(book.getGenres()).isNotNull();
                    return true;
                });
    }

    private void assertThatGenresAreNotEmpty(List<MongoGenre> genres) {
        assertThat(genres).isNotEmpty()
                .allMatch(genre -> {
                    assertThat(genre.getId()).isNotNull();
                    assertThat(genre.getName()).isNotNull();
                    return true;
                });
    }

    private void assertThatAuthorsAreNotEmpty(List<MongoAuthor> authors) {
        assertThat(authors).isNotEmpty()
                .allMatch(author -> {
                    assertThat(author.getId()).isNotNull();
                    assertThat(author.getFullName()).isNotNull();
                    return true; // Важно вернуть true, чтобы продолжить проверку
                });
    }
}
