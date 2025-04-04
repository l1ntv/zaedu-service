package ru.tbank.zaedu.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
