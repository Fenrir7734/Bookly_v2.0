package com.fenrir.masterdetail.controller.advice;

import com.fenrir.masterdetail.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<ConstraintViolationInfo> constraintViolations = ex.getConstraintViolations().stream()
                .map(ConstraintViolationInfo::from)
                .toList();
        ErrorMessage message = new ConstraintViolationErrorMessage(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                "Constraint Violation",
                constraintViolations
        );
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ErrorMessage> handleUnexpectedException(Exception e, WebRequest request) {
        logger.error(e.getMessage());
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                "An unexpected error occurred"
        );
        e.printStackTrace();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ PasswordMismatchException.class, DuplicateCredentialsException.class })
    public ResponseEntity<ErrorMessage> handleCredentialsException(RuntimeException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ AuthenticationException.class  })
    public ResponseEntity<ErrorMessage> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }
}
