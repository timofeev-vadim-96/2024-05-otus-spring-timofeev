package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.context.aot.TestRuntimeHintsRegistrar;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.TestResult;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final LocalizedIOService ioService;

    @Override
    public void showResult(TestResult testResult) {
        ioService.printLine("");
        ioService.printLineLocalized("ResultService.test.results");
        ioService.printFormattedLineLocalized("ResultService.student",
                testResult.getStudent().getFullName());
        ioService.printFormattedLineLocalized("ResultService.answered.questions.count",
                testResult.getAnsweredQuestions().size());
        ioService.printFormattedLineLocalized("ResultService.right.answers.count",
                testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            ioService.printLineLocalized("ResultService.passed.test");
            return;
        }
        ioService.printLineLocalized("ResultService.fail.test");
    }

    @Override
    public void showRightAnswers(TestResult result) {
        ioService.printFormattedLineLocalized("ResultService.student",
                result.getStudent().getFullName());
        Map<Question, Boolean> answeredQuestions = result.getAnsweredQuestions();
        for (Question question: answeredQuestions.keySet()) {
            ioService.printFormattedLineLocalized("ResultService.question", question);
            ioService.printFormattedLineLocalized("ResultService.student.isRightAnswer",
                    answeredQuestions.get(question));
        }
    }
}
