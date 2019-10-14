package com.tmavn.sample.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.persistence.PersistenceException;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.tmavn.sample.handler.RestExceptionHandler;

public class RestExceptionHandlerTest {

    @Test
    public void testPersistenceExceptionHandler_success() {
        PersistenceException e = mock(PersistenceException.class);
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        Mockito.when(e.getMessage()).thenReturn("Test");

        ResponseEntity<?> resultResponse = restExceptionHandler.persistenceExceptionHandler(e);
        ResponseEntity<?> expectResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        assertEquals(expectResponse, resultResponse);
    }

    @Test
    public void testMethodNotSupportExceptionHandler_success() {
        HttpRequestMethodNotSupportedException e = mock(HttpRequestMethodNotSupportedException.class);
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();
        Mockito.when(e.getMessage()).thenReturn("Test");

        ResponseEntity<?> resultResponse = restExceptionHandler.methodNotSupportExceptionHandler(e);
        ResponseEntity<?> expectResponse = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(e.getMessage());

        assertEquals(expectResponse, resultResponse);
    }

    @Test
    public void testExceptionHandler_success() {
        Exception e = mock(Exception.class);
        RestExceptionHandler restExceptionHandler = new RestExceptionHandler();

        ResponseEntity<?> resultResponse = restExceptionHandler.exceptionHandler(e);
        ResponseEntity<?> expectResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        assertEquals(expectResponse, resultResponse);
    }
}
