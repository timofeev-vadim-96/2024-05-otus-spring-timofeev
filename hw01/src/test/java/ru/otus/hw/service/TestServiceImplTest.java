package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestServiceImplTest {
    static TestService service;
    static IOService ioService;
    static QuestionDao dao;

    @BeforeAll
    static void setUp(){
        ioService = mock(IOService.class);
        dao = mock(QuestionDao.class);
        service = new TestServiceImpl(ioService, dao);
    }

    @Test
    void executeTest() {
        when(dao.findAll()).thenReturn(Collections.EMPTY_LIST);
        doNothing().when(ioService).printLine(anyString());

        service.executeTest();

        verify(dao, times(1)).findAll();
        verify(ioService, atLeastOnce()).printLine(anyString());
        verify(ioService, atLeastOnce()).printFormattedLine(anyString());
    }
}