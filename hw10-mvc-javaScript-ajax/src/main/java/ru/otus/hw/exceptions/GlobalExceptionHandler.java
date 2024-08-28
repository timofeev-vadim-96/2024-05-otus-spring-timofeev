package ru.otus.hw.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Ловит исключения при нарушении уникальности полей
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Void> handleDataAccessException() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
