package com.coditory.sherlock;

public class SherlockException extends RuntimeException {
    public SherlockException(String message) {
        super(message);
    }

    public SherlockException(String message, Throwable cause) {
        super(message, cause);
    }
}
