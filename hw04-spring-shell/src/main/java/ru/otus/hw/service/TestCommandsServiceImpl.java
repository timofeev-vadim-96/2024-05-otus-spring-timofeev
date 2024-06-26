package ru.otus.hw.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.TestResult;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Command(group = "app-commands")
public class TestCommandsServiceImpl implements TestCommandsService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @Getter
    private TestResult result;

    @Override
    @Command(command = "run", alias = "r")
    public void run() {
        var student = studentService.determineCurrentStudent();
        result = testService.executeTestFor(student);
        resultService.showResult(result);
    }

    @Override
    @Command(command = "result", alias = "res")
    @CommandAvailability(provider = "showResultCommandAvailability")
    public void showRightAnswers() {
        resultService.showRightAnswers(result);
    }
}
