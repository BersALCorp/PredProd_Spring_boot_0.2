package web.exception;

public class UserControllerException extends RuntimeException {
    public UserControllerException() {
        super();
    }

    public UserControllerException(String message) {
        super("Error in UserController: " + message);
    }

    public UserControllerException(String message, Throwable cause) {
        super("Error in UserController: " + message, cause);
    }
}
