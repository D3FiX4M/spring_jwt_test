package com.example.security.services;

import com.example.security.Exceptions.ExistException;
import com.example.security.Exceptions.NotFoundException;
import com.example.security.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler
    public ResponseEntity<MessageResponse> RunTimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponse(exception.getMessage()));
    }


    @ExceptionHandler
    public ResponseEntity<MessageResponse> NotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<MessageResponse> ExistException(ExistException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponse(exception.getMessage()));
    }




}

