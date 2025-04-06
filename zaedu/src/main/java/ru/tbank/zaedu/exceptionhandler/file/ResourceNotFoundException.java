package ru.tbank.zaedu.exceptionhandler.file;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
