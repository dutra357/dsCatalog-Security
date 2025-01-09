package com.dutra.dsCatalog.controller.handlers;

import com.dutra.dsCatalog.controller.exceptions.Error;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> entityNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Error error = new Error(Instant.now(), status.value(), "Resource not found.", exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<Error> dataBaseException(DataBaseException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        Error error = new Error(Instant.now(), status.value(), "DataBase exception.", exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
