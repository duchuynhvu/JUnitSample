package com.tmavn.sample.handler;

import javax.persistence.PersistenceException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = { PersistenceException.class })
    public ResponseEntity<?> persistenceExceptionHandler(PersistenceException e) {
        log.debug("IN - persistenceExceptionHandler");
        log.error("Exception: ", e);
        log.debug("OUT - persistenceExceptionHandler");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<?> methodNotSupportExceptionHandler(HttpRequestMethodNotSupportedException e){
        log.debug("OUT - methodNotSupportExceptionHandler");
        log.error("Exception: ", e);
        log.debug("OUT - methodNotSupportExceptionHandler");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage());
    }
    
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<?> exceptionHandler(Exception e) {
        log.debug("OUT - exceptionHandler");
        log.error("Exception: ", e);
        log.debug("OUT - exceptionHandler");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    

}
