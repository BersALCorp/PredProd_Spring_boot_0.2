package web.exception;

public class LogoutControllerException extends RuntimeException {
    public LogoutControllerException() {
        super();
    }

    public LogoutControllerException(String message) {
        super(message);
    }

    public LogoutControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
