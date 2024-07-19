package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@Slf4j
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        String sqlForBook = "select b from Book b left join fetch b.genres where b.id = :id";
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");

        TypedQuery<Book> query = em.createQuery(sqlForBook, Book.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList().isEmpty() ? Optional.empty() : Optional.of(query.getResultList().get(0));
    }

    @Override
    public List<Book> findAll() {
        String sqlForBook = "select b from Book b left join fetch b.genres";
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");

        TypedQuery<Book> query = em.createQuery(sqlForBook, Book.class);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        }
        Book merged = em.merge(book);
        Hibernate.initialize(merged.getGenres());
        Hibernate.initialize(merged.getAuthor());
        return merged;
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }
}
