package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("контроллер для работы с книгами")
class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private BookDto book;

    private BookViewDto dto;

    @BeforeEach
    void setUp() {
        long bookId = 1L;
        AuthorDto author = new AuthorDto(1L, "Author_1");
        book = new BookDto(bookId, "Book_1", author, List.of(new GenreDto(1L, "Genre_1")));
        dto = new BookViewDto(bookId, book.getTitle(), author.getId(), Set.of(1L));
    }

    @Test
    void get() throws Exception {
        long id = book.getId();
        when(bookService.findById(id)).thenReturn(book);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)));
        verify(bookService, times(1)).findById(id);
    }

    @Test
    void update() throws Exception {
        when(bookService.update(book.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(book);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)));

        verify(bookService, times(1))
                .update(book.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void updateInvalid() throws Exception {
        when(bookService.update(dto.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenThrow(new EntityNotFoundException("exc"));

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1))
                .update(dto.getId(), dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void create() throws Exception {
        when(bookService.create(dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenReturn(book);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(book)));

        verify(bookService, times(1))
                .create(dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void createInvalid() throws Exception {
        when(bookService.create(dto.getTitle(), dto.getAuthorId(), dto.getGenres()))
                .thenThrow(new EntityNotFoundException("exc"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1))
                .create(dto.getTitle(), dto.getAuthorId(), dto.getGenres());
    }

    @Test
    void getAll() throws Exception {
        List<BookDto> expected = List.of(book);
        when(bookService.findAll()).thenReturn(expected);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
        verify(bookService, times(1)).findAll();
    }

    @Test
    void delete() throws Exception {
        long id = 1L;
        doNothing().when(bookService).deleteById(id);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/book/{id}", id))
                .andExpect(status().isOk());
        verify(bookService, times(1)).deleteById(id);
    }
}