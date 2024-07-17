package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph("author-entity-graph")
    @Query("select b from Book b left join fetch b.genres where b.id = :id")
    Optional<Book> findById(long id);

    @EntityGraph("author-entity-graph")
    @Query("select b from Book b left join fetch b.genres")
    @Override
    List<Book> findAll();
}
