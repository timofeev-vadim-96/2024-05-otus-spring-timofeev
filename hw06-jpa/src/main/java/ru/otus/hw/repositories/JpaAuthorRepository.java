package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAuthorRepository implements AuthorRepository {
    @PersistenceContext
    private final EntityManager em;

    public JpaAuthorRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Author> findAll() {
        String jpql = "select a from Author a";

        TypedQuery<Author> query = em.createQuery(jpql, Author.class);
        return query.getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        Author author = em.find(Author.class, id);

        return Optional.ofNullable(author);
    }
}
