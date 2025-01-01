//package ru.otus.hw.controllers.rest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import ru.otus.hw.services.GenreService;
//import ru.otus.hw.services.dto.GenreDto;
//
//import java.util.List;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(value = GenreController.class)
//@DisplayName("Контроллер для работы с жанрами")
//class GenreControllerTest {
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @MockBean
//    private GenreService genreService;
//
//    @Test
//    void getAll() throws Exception {
//        List<GenreDto> expected = List.of(new GenreDto(1L, "Genre_1"));
//
//        when(genreService.findAll()).thenReturn(expected);
//
//        mvc.perform(MockMvcRequestBuilders.get("/api/v1/genre"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(mapper.writeValueAsString(expected)));
//        verify(genreService, times(1)).findAll();
//    }
//}