package com.machine.exception;

public class NotAvailableException extends StoreException {

    public NotAvailableException(String ingredient) {
        super(ingredient);
    }
}
