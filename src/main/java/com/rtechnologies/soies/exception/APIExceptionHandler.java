package com.rtechnologies.soies.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException (NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.toString());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleIllegalArgumentException (AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
    }

//    @ExceptionHandler(value = IllegalArgumentException.class)
//    protected ResponseEntity<Object> handleRuntimeException(IllegalArgumentException ex, WebRequest request) {
//        // Handle the exception and create a response entity
//        return new ResponseEntity<>("error here", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
