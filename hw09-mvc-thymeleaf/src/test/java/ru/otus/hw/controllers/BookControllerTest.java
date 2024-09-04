package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.dto.BookViewDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
@DisplayName("контроллер для работы с книгами")
class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    CommentService commentService;

    @MockBean
    AuthorService authorService;

    @MockBean
    GenreService genreService;

    BookDto book;

    @BeforeEach
    void setUp() {
        book = new BookDto(
                1L, "title", new AuthorDto(), List.of(new GenreDto(1L, "Genre_1")));
        when(bookService.findById(1L)).thenReturn(book);
    }

    @Test
    void getBookPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/book/book_page/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("comments"));
        verify(bookService, times(1)).findById(book.getId());
    }

    @Test
    void getEditPage() throws Exception {
        BookDto book = new BookDto(1L, "title", new AuthorDto(), List.of());
        when(bookService.findById(1L)).thenReturn(book);
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/book/edit_page/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("dto"));
        verify(bookService, times(1)).findById(1L);
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    void update() throws Exception {
        BookViewDto book = new BookViewDto(
                "title", //valid title
                1L,
                Set.of(1L));

        mvc.perform(MockMvcRequestBuilders.post("/book/edit/{id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", book.getTitle())
                        .param("authorId", String.valueOf(book.getAuthorId()))
                        .param("genres", book.getGenres()
                                .toString().replace("[", "")
                                .replace("]", "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1))
                .update(
                        1L,
                        book.getTitle(),
                        book.getAuthorId(),
                        book.getGenres());
    }

    @Test
    void updateInvalid() throws Exception {
        BookViewDto book = new BookViewDto(
                "t", //invalid title
                1L,
                Set.of(1L));

        mvc.perform(MockMvcRequestBuilders.post("/book/edit/{id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", book.getTitle())
                        .param("authorId", String.valueOf(book.getAuthorId()))
                        .param("genres", book.getGenres()
                                .toString().replace("[", "")
                                .replace("]", "")))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
        verify(bookService, times(1)).findById(1L);
    }

    @Test
    void create() throws Exception {
        BookViewDto book = new BookViewDto(
                "title", //valid title
                1L,
                Set.of(1L));

        mvc.perform(MockMvcRequestBuilders.post("/book/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", book.getTitle())
                        .param("authorId", String.valueOf(book.getAuthorId()))
                        .param("genres", book.getGenres()
                                .toString().replace("[", "")
                                .replace("]", "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1))
                .create(
                        book.getTitle(),
                        book.getAuthorId(),
                        book.getGenres());
    }

    @Test
    void createInvalid() throws Exception {
        BookViewDto book = new BookViewDto(
                "t", //invalid title
                1L,
                Set.of(1L));

        mvc.perform(MockMvcRequestBuilders.post("/book/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", book.getTitle())
                        .param("authorId", String.valueOf(book.getAuthorId()))
                        .param("genres", book.getGenres()
                                .toString().replace("[", "")
                                .replace("]", "")))
                .andExpect(status().isOk())
                .andExpect(view().name("create"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    void getCreatePage() throws Exception {
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/book/create_page"))
                .andExpect(status().isOk())
                .andExpect(view().name("create"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attributeExists("authors"));
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    void getAll() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("books"));
        verify(bookService, times(1)).findAll();
    }

    @Test
    void delete() throws Exception {
        long id = 1L;
        doNothing().when(bookService).deleteById(id);

        mvc.perform(MockMvcRequestBuilders.post("/book/delete/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService, times(1)).deleteById(1);
    }
}