package com.potatorider.exception;

import reactor.util.retry.Retry;
import reactor.util.retry.Retry.RetrySignal;

public class RetryExhaustedException extends RuntimeException {

    public RetryExhaustedException(final RetrySignal retrySignal) {
        super(retrySignal.failure());
    }
}
