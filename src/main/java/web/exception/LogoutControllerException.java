package web.exception;

public class LogoutControllerException extends RuntimeException {
    public LogoutControllerException() {
        super();
    }

    public LogoutControllerException(String message) {
        super("Error in LogoutController: " + message);
    }

    public LogoutControllerException(String message, Throwable cause) {
        super("Error in LogoutController: " + message, cause);
    }
}
