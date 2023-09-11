package web.exception;

public class UserServiceException extends Exception {
    public UserServiceException() {
        super();
    }

    public UserServiceException(String message) {
        super("Error in UserService: " + message);
    }

    public UserServiceException(String message, Throwable cause) {
        super("Error in UserService: " + message, cause);
    }
}
