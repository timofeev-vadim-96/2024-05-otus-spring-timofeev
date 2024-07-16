package ru.otus.hw.repositories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcBookRepository implements BookRepository {
    private final GenreRepository genreRepository;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Book> findById(long id) {
        String sqlForBook = "select books.id as b_id, books.title, books.author_id, " +
                "authors.full_name, " +
                "genres.id as g_id, genres.name as genre_name\n" +
                "                    from books\n" +
                "                    join authors\n" +
                "                    on books.author_id = authors.id\n" +
                "                    join books_genres\n" +
                "                    on books.id = books_genres.book_id\n" +
                "                    join genres\n" +
                "                    on books_genres.genre_id = genres.id\n" +
                "                    where books.id = :id";
        List<Book> book = jdbc.query
                (sqlForBook, Map.of("id", id), new BookWithGenresRowMapper());

        if (book.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(book.get(0));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    @Transactional
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "delete from books where id = :id";
        jdbc.update(sql, Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = "select books.id, books.title, books.author_id, authors.full_name " +
                "from books " +
                "join authors " +
                "on books.author_id = authors.id";

        return jdbc.query(sql, Map.of(), new BookWithoutGenresRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "select book_id, genre_id from books_genres";

        return jdbc.query(sql, Map.of(), new RowMapper<BookGenreRelation>() {
            @Override
            public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new BookGenreRelation(
                        rs.getLong("book_id"),
                        rs.getLong("genre_id")
                );
            }
        });
    }

    /**
     * Добавить жанры книге в соответствии со связями
     */
    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Collections.sort(genres, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));

        for (Book book : booksWithoutGenres) {
            List<Long> bookGenresIds = relations.stream()
                    .filter(rl -> rl.bookId == book.getId())
                    .map(BookGenreRelation::genreId)
                    .toList();

            List<Genre> bookGenres = new ArrayList<>();
            for (Long id : bookGenresIds) {
                int index = Collections
                        .binarySearch(genres.stream()
                                        .map(g -> g.getId()).collect(Collectors.toList()),
                                id,
                                ((index1, index2) -> Long.compare(index1, index2)));
                bookGenres.add(genres.get(index));
            }

            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        KeyHolder kh = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValues(Map.of("title", book.getTitle(), "authorId", book.getAuthor().getId()));

        String sql = "insert into books (title, author_id) values (:title, :authorId)";
        jdbc.update(sql, parameterSource, kh);

        book.setId((Long) kh.getKeys().get("id"));
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private Book update(Book book) {
        String sql = "update books set title = :title, author_id = :authorId where id = :id";

        int updated = jdbc.update(sql, Map.of(
                        "title", book.getTitle(),
                        "authorId", book.getAuthor().getId(),
                        "id", book.getId()
                )
        );

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id = %d not found".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(book.getGenres().toArray());
        String sql = "insert into books_genres (book_id, genre_id) values (%d, :id)".formatted(book.getId());

        jdbc.batchUpdate(sql, batch);
    }

    private void removeGenresRelationsFor(Book book) {
        Optional<Book> optionalBook = findById(book.getId());
        if (optionalBook.isEmpty()) {
            throw new EntityNotFoundException("Book with id = %s not found".formatted(book.getId()));
        }

        String sql = "delete from books_genres " +
                "where book_id = :id";

        jdbc.update(sql, Map.of("id", book.getId()));
    }

    private static class BookWithoutGenresRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(
                    rs.getLong("author_id"),
                    rs.getString("full_name")
            );

            return new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    author,
                    new ArrayList<>());
        }
    }

    private static class BookWithGenresRowMapper implements RowMapper<Book> {
        @Getter
        private final Book book = new Book();

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            if (book.getId() == 0) {
                book.setId(rs.getLong("b_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(
                        new Author(
                                rs.getLong("author_id"),
                                rs.getString("full_name")));
                book.setGenres(new ArrayList<>());
            }
            Genre genre = new Genre(
                    rs.getLong("g_id"),
                    rs.getString("genre_name"));

            book.getGenres().add(genre);

            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
