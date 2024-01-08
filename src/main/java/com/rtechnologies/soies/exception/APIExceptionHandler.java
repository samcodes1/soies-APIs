package com.rtechnologies.soies.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException (NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.toString());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleIllegalArgumentException (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
    }
}
