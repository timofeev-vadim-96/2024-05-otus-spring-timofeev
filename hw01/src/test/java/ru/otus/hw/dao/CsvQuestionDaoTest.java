package ru.otus.hw.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {
    static TestFileNameProvider fileNameProvider;
    static CsvQuestionDao dao;

    @BeforeAll
    static void setUp(){
        fileNameProvider = mock(TestFileNameProvider.class);
        dao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    void findAllPositive() {
        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");

        List<Question> all = dao.findAll();

        assertFalse(all.isEmpty());
    }

    @Test
    void findAllNegative() {
        when(fileNameProvider.getTestFileName()).thenReturn("wrong-filename.csv");

        Assertions.assertThatThrownBy(() -> dao.findAll()).isInstanceOf(QuestionReadException.class);
    }
}