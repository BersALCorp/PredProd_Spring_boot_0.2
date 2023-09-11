package web.exception;

public class AdminControllerException extends RuntimeException {
    public AdminControllerException() {
        super();
    }

    public AdminControllerException(String message) {
        super("Error in AdminController: " + message);
    }

    public AdminControllerException(String message, Throwable cause) {
        super("Error in AdminController: " + message, cause);
    }
}
