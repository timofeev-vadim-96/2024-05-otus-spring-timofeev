package ru.otus.hw.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TestResult {
    private final Student student;

    private final Map<Question, Boolean> answeredQuestions;

    private int rightAnswersCount;

    public TestResult(Student student) {
        this.student = student;
        this.answeredQuestions = new HashMap<>();
    }

    public void applyAnswer(Question question, boolean isRightAnswer) {
        answeredQuestions.put(question, isRightAnswer);
        if (isRightAnswer) {
            rightAnswersCount++;
        }
    }
}
