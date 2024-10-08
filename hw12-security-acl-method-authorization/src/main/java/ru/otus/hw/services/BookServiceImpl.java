package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.acl.AclWrapperService;
import ru.otus.hw.services.dto.BookDto;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final AclWrapperService acl;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("canRead(#id, T(ru.otus.hw.services.dto.BookDto))")
//    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));

        return new BookDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        books.sort(Comparator.comparingLong(Book::getId));
        return books.stream().map(BookDto::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDto create(String title, long authorId, Set<Long> genresIds) {
        Book created = save(null, title, authorId, genresIds);

        BookDto dto = new BookDto(created);

        acl.createPermission(dto, BasePermission.READ);

        return dto;
    }

    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, Set<Long> genresIds) {
        Book updated = save(id, title, authorId, genresIds);

        return new BookDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Book save(Long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = getGenres(genresIds);

        Book book;
        if (id != null) {
            book = bookRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with id = %d not found".formatted(id)));
            book.setTitle(title);
            book.setAuthor(author);
            book.setGenres(genres);
        } else {
            book = new Book(null, title, author, genres);
        }

        return bookRepository.save(book);
    }

    private Set<Genre> getGenres(Set<Long> genresIds) {
        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        return genres;
    }
}