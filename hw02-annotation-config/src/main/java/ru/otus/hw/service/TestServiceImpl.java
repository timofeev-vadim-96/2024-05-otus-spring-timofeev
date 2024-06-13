package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            ioService.printLine(question.text());
            var isAnswerValid = isAnswerValid(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean isAnswerValid(Question question) {
        int rightChoice = getRightChoiceNumber(question);
        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printLine(String.format("%d: %s", i + 1, answers.get(i).text()));
        }
        int studentChoice = ioService.readIntForRangeWithPrompt(1, answers.size(), "Enter answer:", "There is no such answer choice");
        return studentChoice == rightChoice + 1;
    }

    /**
     * @return index of right answer or returns -1 if there is not right answer at all
     */
    private int getRightChoiceNumber(Question question){
        List<Answer> answers = question.answers();
        Optional<Answer> answer = answers.stream().filter(Answer::isCorrect).findFirst();
        return answer.map(answers::indexOf).orElse(-1);
    }
}
