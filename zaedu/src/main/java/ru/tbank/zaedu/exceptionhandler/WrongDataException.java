package src.main.java.ru.tbank.zaedu.exceptionhandler;

public class WrongDataException extends RuntimeException {
    public WrongDataException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
