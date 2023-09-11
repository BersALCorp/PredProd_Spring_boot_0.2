package web.exception;

public class AuthControllerException extends RuntimeException {
    public AuthControllerException() {
        super();
    }

    public AuthControllerException(String message) {
        super("Error in AuthController: " + message);
    }

    public AuthControllerException(String message, Throwable cause) {
        super("Error in AuthController: " + message, cause);
    }
}
