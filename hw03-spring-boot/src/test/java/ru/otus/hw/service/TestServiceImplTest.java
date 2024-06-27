package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestServiceImplTest {
    static TestService service;
    static LocalizedIOService ioService;
    static QuestionDao dao;

    @BeforeAll
    static void setUp(){
        ioService = mock(LocalizedIOService.class);
        dao = mock(QuestionDao.class);
        service = new TestServiceImpl(ioService, dao);
    }

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