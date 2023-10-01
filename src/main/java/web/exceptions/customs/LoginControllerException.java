package web.exceptions.customs;

public class LoginControllerException extends RuntimeException {
    public LoginControllerException() {
        super();
    }

    public LoginControllerException(String message) {
        super(message);
    }

    public LoginControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
