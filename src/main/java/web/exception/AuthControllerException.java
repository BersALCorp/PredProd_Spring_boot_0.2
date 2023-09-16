package web.exception;

public class AuthControllerException extends RuntimeException {
    public AuthControllerException() {
        super();
    }

    public AuthControllerException(String message) {
        super(message);
    }

    public AuthControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
