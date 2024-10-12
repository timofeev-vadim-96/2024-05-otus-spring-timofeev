package ru.otus.hw.controllers.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.security.SecurityConfig;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GenreController.class)
@Import(SecurityConfig.class)
@DisplayName("Контроллер для работы с жанрами")
public class GenreControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenreController genreController;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(genreController).getAll();
    }

    @Test
    void getAllWithoutAuthorization() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllByUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllByAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                .andExpect(status().isOk());
    }
}
