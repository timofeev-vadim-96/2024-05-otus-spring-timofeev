package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GenreController.class)
@Import(SecurityConfig.class)
@DisplayName("Контроллер для работы с жанрами")
public class GenreControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
                .andExpect(status().isFound());
    }
}
