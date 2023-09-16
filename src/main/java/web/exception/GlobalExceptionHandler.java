package web.exception;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AdminControllerException.class)
    public ResponseEntity<Object> handleAdminControllerException(AdminControllerException e, WebRequest request) {
        log.error("Error in AdminController: " + e.getMessage());
        var body = "Error in AdminController: " + e.getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(AuthControllerException.class)
    public ResponseEntity<Object> handleAuthControllerException(AuthControllerException e, WebRequest request) {
        log.error("Error in AuthController: " + e.getMessage());
        var body = "Error in AuthController: " + e.getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(LogoutControllerException.class)
    public ResponseEntity<Object> handleLogoutControllerException(LogoutControllerException e, WebRequest request) {
        log.error("Error in LogoutController: " + e.getMessage());
        var body = "Error in LogoutController: " + e.getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(UserControllerException.class)
    public ResponseEntity<Object> handleUserControllerException(UserControllerException e, WebRequest request) {
        log.error("Error in UserController: " + e.getMessage());
        var body = "Error in UserController: " + e.getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(UserServiceException.class)
    protected ResponseEntity<Object> handleUserServiceException(UserServiceException e, WebRequest request) {
        log.error("Error in UserService", e);
        var body = "Error in UserService: " + e.getMessage();
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleExceptionInternal(@NotNull Exception ex, Object body, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        log.error("An error has occurred: ", ex);
        log.info(request.getHeader("User-Agent"));
        log.info(request.getDescription(true));
        log.info(request.isSecure());
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
