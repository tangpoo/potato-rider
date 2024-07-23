package com.potatorider.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(
            value = {
                IllegalArgumentException.class,
                IllegalStateException.class,
                DeliveryNotFoundException.class
            })
    public ResponseEntity<String> handleBadRequests(Exception ex) {
        logException(ex);
        var error = ex.getMessage();
        log.error("Error is : {}", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(value = {RetryExhaustedException.class})
    public ResponseEntity<String> handleRetryException(RetryExhaustedException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<String> handleInternalServerError(Throwable ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(value = {WebExchangeBindException.class})
    public ResponseEntity<String> handleConstraintViolation(WebExchangeBindException ex) {
        logException(ex);
        var error =
                ex
                        .getBindingResult().getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .sorted()
                                .collect(Collectors.joining("/"));
        log.error("Error is : {}", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private void logException(final Throwable ex) {
        log.error("{} caught by advice : {}", ex.getClass().getName(), ex.getMessage(), ex);
    }
}
