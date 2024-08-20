package ru.otus.hw.controllers.classic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = PageController.class)
class PageControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void create() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/create"))
                .andExpect(view().name("create"));
    }

    @Test
    void edit() throws Exception {
        long id = 1L;
        mvc.perform(MockMvcRequestBuilders.get("/edit/{id}", id))
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("id", id));
    }

    @Test
    void list() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(view().name("books"));
    }

    @Test
    void book() throws Exception {
        long id = 1L;
        mvc.perform(MockMvcRequestBuilders.get("/book/{id}", id))
                .andExpect(view().name("book"))
                .andExpect(model().attribute("id", id));
    }
}