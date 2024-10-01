package ru.otus.hw.services.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.dto.BookDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookServiceAclSecurityTest {
    private static final int ADMIN_BOOKS_SIZE = 3;

    private static final int USER_BOOKS_SIZE = 2;

    @Autowired
    private BookService bookService;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    @WithMockUser(roles = "ADMIN")
    void findByIdByAdmin(long id) {
        BookDto book = bookService.findById(id);

        assertNotNull(book);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2})
    @WithMockUser(username = "user")
    void findByIdByUserGranted(long id) {
        BookDto book = bookService.findById(id);

        assertNotNull(book);
    }

    @Test
    @WithMockUser(username = "user")
    void findByIdByUserDenying() {
        assertThrows(AccessDeniedException.class, () -> bookService.findById(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllByAdmin() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty().size().isEqualTo(ADMIN_BOOKS_SIZE);
    }

    @Test
    @WithMockUser(username = "user")
    void findAllByUser() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty().size().isEqualTo(USER_BOOKS_SIZE);
    }

    @Test
    @WithMockUser(username = "unknown")
    void findAllByUnknown() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isEmpty();
    }
}
