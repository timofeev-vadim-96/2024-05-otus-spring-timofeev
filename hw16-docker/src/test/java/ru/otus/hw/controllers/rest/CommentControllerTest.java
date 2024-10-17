package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CommentController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("контроллер для работы с комментариями")
class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    private CommentDto comment;
    private CommentViewDto dto;

    @BeforeEach
    void setUp() {
        long bookId = 1L;
        AuthorDto author = new AuthorDto(1L, "Author_1");
        BookDto book = new BookDto(bookId, "Book_1", author, List.of(new GenreDto(1L, "Genre_1")));
        comment = new CommentDto(1L, "Larger or equals 10 symbols", book);
        List<CommentDto> comments = List.of(comment);
        dto = new CommentViewDto(comment.getText(), bookId);

        Mockito.when(bookService.findById(bookId)).thenReturn(book);
        Mockito.when(commentService.findAllByBookId(bookId)).thenReturn(comments);
    }

    @Test
    void createComment() throws Exception {
        long bookId = dto.getBookId();
        String expectedContent = mapper.writeValueAsString(comment);
        when(commentService.create(dto.getText(), dto.getBookId())).thenReturn(comment);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedContent));

        Mockito.verify(commentService, Mockito.times(1)).create(dto.getText(), bookId);
    }

    @Test
    void createCommentInvalid() throws Exception {
        long bookId = dto.getBookId();
        when(commentService.create(dto.getText(), dto.getBookId())).thenThrow(new EntityNotFoundException("exc"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        Mockito.verify(commentService, Mockito.times(1)).create(dto.getText(), bookId);
    }

    @Test
    void deleteComment() throws Exception {
        long id = comment.getId();
        doNothing().when(commentService).deleteById(anyLong());

        mvc.perform(delete("/api/v1/comment/{id}", id))
                .andExpect(status().isOk());
        verify(commentService, times(1)).deleteById(id);
    }

    @Test
    void getAll() throws Exception {
        long bookId = dto.getBookId();

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(comment))));
        verify(commentService, times(1)).findAllByBookId(bookId);
    }
}