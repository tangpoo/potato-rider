package com.potatorider.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = {
        IllegalStateException.class,
        IllegalArgumentException.class,
        DeliveryNotFountException.class
    })
    public ResponseEntity<String> handleBadRequests(Exception ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = {RetryExhaustedException.class})
    public ResponseEntity<String> handleRetryException(RetryExhaustedException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    private void logException(final Exception ex) {
        log.error("{} caught by advice : {}", ex.getClass().getName(), ex.getMessage(), ex);
    }
}
