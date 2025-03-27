package ru.tbank.zaedu.exceptionhandler;

public class ConflictResourceException extends RuntimeException {

    public ConflictResourceException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
