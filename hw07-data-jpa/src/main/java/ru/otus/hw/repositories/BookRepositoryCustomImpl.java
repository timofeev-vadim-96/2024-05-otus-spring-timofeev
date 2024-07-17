package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private final EntityManager em;

    private final BookRepository bookRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.merge(book);
            return book;
        }
        Book merged = em.merge(book);
        Hibernate.initialize(merged.getGenres());
        Hibernate.initialize(merged.getAuthor());
        return merged;
    }

    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}
