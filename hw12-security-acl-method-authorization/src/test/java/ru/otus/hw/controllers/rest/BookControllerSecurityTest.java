package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
@DisplayName("контроллер для работы с книгами")
public class BookControllerSecurityTest {
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

    @MockBean
    private UserDetailsService userDetailsService;


    @Test
    void get() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book/{id}", 1L))
                .andExpect(status().isFound());
    }

    @Test
    void getAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/book"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void create() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/book", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void update() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/api/v1/book", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/book/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}
