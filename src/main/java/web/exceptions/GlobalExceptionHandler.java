package web.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import web.exceptions.customs.ApiControllerException;
import web.exceptions.customs.LoginControllerException;
import web.exceptions.customs.ViewControllerException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiControllerException.class)
    public ResponseEntity<Object> handleApiControllerException(ApiControllerException e, WebRequest request) {
        return handleException(e, request, "ApiController");
    }

    @ExceptionHandler(LoginControllerException.class)
    public ResponseEntity<Object> handleAuthControllerException(LoginControllerException e, WebRequest request) {
        return handleException(e, request, "AuthController");
    }

    @ExceptionHandler(ViewControllerException.class)
    public ResponseEntity<Object> handleAuthControllerException(ViewControllerException e, WebRequest request) {
        return handleException(e, request, "ViewController");
    }

    private ResponseEntity<Object> handleException(RuntimeException e, WebRequest request, String controllerName) {
        log.error("Error in " + controllerName + ": " + e.getMessage());
        var body = e.getCause().getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleExceptionInternal(@NotNull Exception ex, Object body, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        logExceptionDetails(ex, request);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private void logExceptionDetails(Exception ex, WebRequest request) {
        log.error("An error has occurred: ", ex);
        log.info(request.getHeader("User-Agent"));
        log.info(request.getDescription(true));
        log.info(String.valueOf(request.isSecure()));
    }
}
