package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = {CommentController.class})
@DisplayName("контроллер для работы с комментариями")
class CommentControllerTest {
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

    @BeforeEach
    void setUp() {
        BookDto book = new BookDto(1L, "Book Title", new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre")));
        List<CommentDto> comments = List.of(new CommentDto(1L, "Comment 1", book));
        when(bookService.findById(1L)).thenReturn(book);
        when(commentService.findAllByBookId(1L)).thenReturn(comments);
    }

    @Test
    void createComment() throws Exception {
        long bookId = 1L;
        String comment = "New Comment"; // valid dto

        mvc.perform(MockMvcRequestBuilders.post("/comment/create/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", comment)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/book_page/" + bookId));

        Mockito.verify(commentService, Mockito.times(1)).create(comment, bookId);
    }

    @Test
    void createCommentInvalid() throws Exception {
        long bookId = 1L;

        mvc.perform(MockMvcRequestBuilders.post("/comment/create/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", "") // invalid field
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("genres"));
        verify(commentService, Mockito.never()).create(anyString(), anyLong());
    }

    @Test
    void deleteComment() throws Exception {
        long bookId = 1;
        doNothing().when(commentService).deleteById(anyLong());

        mvc.perform(post("/comment/delete/{id}", 1L)
                        .param("bookId", String.valueOf(bookId))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/book_page/" + bookId));
        verify(commentService, times(1)).deleteById(1L);
    }
}