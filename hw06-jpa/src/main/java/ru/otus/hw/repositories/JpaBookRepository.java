package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {
    @PersistenceContext
    private final EntityManager em;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        String jpql = "select b from Book b left join fetch b.genres where b.id = :id";
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");
        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList().isEmpty() ? Optional.empty() : Optional.of(query.getResultList().get(0));
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
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        }
        em.merge(book);

        return book;
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "select book_id, genre_id from books_genres";

        Query query = em.createNativeQuery(sql, BookGenreRelation.class);

        return query.getResultList();
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Genre> genresMap = genres.stream()
                .collect(Collectors
                        .toMap(Genre::getId, Function.identity()));

        Map<Long, Book> booksMap = booksWithoutGenres.stream()
                .collect(Collectors
                        .toMap(Book::getId, Function.identity()));

        for (BookGenreRelation relation : relations) {
            Book book = booksMap.get(relation.bookId);
            Genre genre = genresMap.get(relation.genreId);
            book.getGenres().add(genre);
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        String jpql = "select b from Book b order by b.id asc";
        EntityGraph<?> authorGraph = em.getEntityGraph("author-entity-graph");
        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        query.setHint(FETCH.getKey(), authorGraph);

        return query.getResultList();
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
