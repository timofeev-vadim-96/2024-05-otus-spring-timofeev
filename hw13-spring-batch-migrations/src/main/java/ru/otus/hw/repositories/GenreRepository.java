package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.relation.Genre;

import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g from Genre g WHERE g.id IN :ids")
    Set<Genre> findAllByIds(@Param("ids") Set<Long> ids);
}
