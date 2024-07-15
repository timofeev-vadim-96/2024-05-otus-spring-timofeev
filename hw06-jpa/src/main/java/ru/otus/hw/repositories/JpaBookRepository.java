package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<Book> findById(long id) {
        try {
            String sqlForBook = "select b from Book b left join fetch b.genres where b.id = :id";
            EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");

            TypedQuery<Book> query = em.createQuery(sqlForBook, Book.class);
            query.setParameter("id", id);
            query.setHint(FETCH.getKey(), entityGraph);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Book> findAll() {
        String sqlForBook = "select b from Book b left join fetch b.genres";
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");

        TypedQuery<Book> query = em.createQuery(sqlForBook, Book.class);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.merge(book);
            return book;
        }
        em.merge(book);
        return findById(book.getId()).get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(long id) {
        String sql = "delete from Book b where b.id = :id";
        Query query = em.createQuery(sql);
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
