package web.exception;

public class UserControllerException extends RuntimeException {
    public UserControllerException() {
        super();
    }

    public UserControllerException(String message) {
        super(message);
    }

    public UserControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
