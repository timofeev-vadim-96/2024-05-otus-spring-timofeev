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
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
@DisplayName("контроллер для работы с комментариями")
public class CommentControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void create() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/comment"))
                .andExpect(status().isFound());
    }

    @Test
    void get() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/comment/{bookId}", 1L))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}
