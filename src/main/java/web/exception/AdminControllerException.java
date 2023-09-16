package web.exception;

public class AdminControllerException extends RuntimeException {
    public AdminControllerException() {
        super();
    }

    public AdminControllerException(String message) {
        super(message);
    }

    public AdminControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
