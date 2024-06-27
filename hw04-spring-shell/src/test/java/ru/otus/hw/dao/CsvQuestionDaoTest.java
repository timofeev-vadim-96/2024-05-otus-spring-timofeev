package ru.otus.hw.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CsvQuestionDao.class})
class CsvQuestionDaoTest {
    @Autowired
    QuestionDao dao;

    @MockBean
    TestFileNameProvider fileNameProvider;

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