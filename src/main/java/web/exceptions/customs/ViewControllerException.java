package web.exceptions.customs;

public class ViewControllerException extends RuntimeException {
    public ViewControllerException() {
        super();
    }

    public ViewControllerException(String message) {
        super(message);
    }

    public ViewControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
