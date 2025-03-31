package src.main.java.ru.tbank.zaedu.exceptionhandler;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
