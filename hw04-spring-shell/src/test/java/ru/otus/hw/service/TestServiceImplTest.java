package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {TestServiceImpl.class})
class TestServiceImplTest {

    @Autowired
    TestService service;

    @MockBean
    LocalizedIOService ioService;

    @MockBean
    QuestionDao dao;

    @Test
    void executeTest() {
        when(dao.findAll()).thenReturn(Collections.EMPTY_LIST);
        doNothing().when(ioService).printLine(anyString());

        service.executeTestFor(new Student("testName", "testLastName"));

        verify(dao, times(1)).findAll();
        verify(ioService, atLeastOnce()).printLine(anyString());
        verify(ioService, atLeastOnce()).printFormattedLineLocalized(anyString());
    }
}