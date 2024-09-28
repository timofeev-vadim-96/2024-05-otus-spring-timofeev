package ru.otus.hw.controllers;

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
@DisplayName("контроллер для работы с комментариями (безопасность)")
public class CommentControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = {"USER"})
    void createCommentForbidden() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/comment/{bookId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteCommentForbidden() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/comment/delete/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}
