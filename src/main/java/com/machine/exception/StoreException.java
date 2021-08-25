package com.machine.exception;

public class StoreException extends Exception {

    public String getItem() {
        return item;
    }

    private String item;

    StoreException(String ingredient) {
        this.item=ingredient;
    }
}
