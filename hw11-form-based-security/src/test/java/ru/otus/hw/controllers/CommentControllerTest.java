package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {CommentController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("контроллер для работы с комментариями")
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    @Test
    void createComment() throws Exception {
        long bookId = 1L;
        String comment = "New Comment"; // valid dto
        BookDto book = new BookDto(bookId, "Book Title", new AuthorDto(1L, "Author_1"),
                List.of(new GenreDto(1L, "Genre")));
        List<CommentDto> comments = List.of(new CommentDto(1L, "Comment 1", book));

        Mockito.when(bookService.findById(bookId)).thenReturn(book);
        Mockito.when(commentService.findAllByBookId(bookId)).thenReturn(comments);

        mvc.perform(MockMvcRequestBuilders.post("/comment/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", comment)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + bookId));

        Mockito.verify(commentService, Mockito.times(1)).create(comment, bookId);
    }

    @Test
    void createCommentInvalid() throws Exception {
        long bookId = 1L;
        BookDto book = new BookDto(bookId, "Book Title", new AuthorDto(), List.of(new GenreDto(1L, "Genre")));
        List<CommentDto> comments = List.of(new CommentDto(1L, "Comment 1", book));

        Mockito.when(bookService.findById(bookId)).thenReturn(book);
        Mockito.when(commentService.findAllByBookId(bookId)).thenReturn(comments);

        mvc.perform(MockMvcRequestBuilders.post("/comment/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("text", "") // invalid field
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    void deleteComment() throws Exception {
        long id = 1;
        doNothing().when(commentService).deleteById(anyLong());

        mvc.perform(post("/comment/delete/{id}", id)
                        .param("bookId", String.valueOf(id))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + id));
    }
}