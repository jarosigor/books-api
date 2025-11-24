package com.morotech.bookApi.exception;

public class InvalidPageException extends RuntimeException {
    public InvalidPageException(String message) {
        super(message);
    }
}
