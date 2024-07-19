package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaGenreRepository implements GenreRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Genre> findAll() {
        String sql = "select g from Genre g";

        TypedQuery<Genre> query = em.createQuery(sql, Genre.class);

        return query.getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        String sql = "SELECT g from Genre g WHERE id IN :ids";

        TypedQuery<Genre> query = em.createQuery(sql, Genre.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }
}
