package web.exceptions.customs;

public class ApiControllerException extends RuntimeException {
    public ApiControllerException() {
        super();
    }

    public ApiControllerException(String message) {
        super(message);
    }

    public ApiControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
