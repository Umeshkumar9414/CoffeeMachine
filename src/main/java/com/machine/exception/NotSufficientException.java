package com.machine.exception;

public class NotSufficientException extends StoreException {

    public NotSufficientException(String ingredient) {
        super(ingredient);
    }
}
