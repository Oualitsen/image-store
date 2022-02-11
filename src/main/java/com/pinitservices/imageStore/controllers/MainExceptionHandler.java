package com.pinitservices.imageStore.controllers;

import java.util.Map;

import com.pinitservices.imageStore.exceptions.ResourceFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(ResourceFoundException.class)
    public ResponseEntity<Object> handleInvalidPasswordException(ResourceFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", String.format("Image %s not found", exception.getImageId())));
    }
}
