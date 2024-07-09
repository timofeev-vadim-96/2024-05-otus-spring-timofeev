package ru.otus.hw.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
@Slf4j
@Command(group = "app-commands")
public class TestCommandsServiceImpl implements TestCommandsService {
    @Getter
    private static TestResult result;

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;


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
    public void result() {
        resultService.showRightAnswers(result);
    }
}
