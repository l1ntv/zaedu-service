package src.main.java.ru.tbank.zaedu.exceptionhandler;

public class LockedUserException extends RuntimeException {

    public LockedUserException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
