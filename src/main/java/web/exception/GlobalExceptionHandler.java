package web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработчик исключений для AdminController
    @ExceptionHandler(AdminControllerException.class)
    public ResponseEntity<String> handleAdminControllerException(AdminControllerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка в AdminController: " + e.getMessage());
    }

    // Обработчик исключений для AuthController
    @ExceptionHandler(AuthControllerException.class)
    public ResponseEntity<String> handleAuthControllerException(AuthControllerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка в AuthController: " + e.getMessage());
    }

    // Обработчик исключений для LogoutController
    @ExceptionHandler(LogoutControllerException.class)
    public ResponseEntity<String> handleLogoutControllerException(LogoutControllerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка в LogoutController: " + e.getMessage());
    }

    // Обработчик исключений для UserController
    @ExceptionHandler(UserControllerException.class)
    public ResponseEntity<String> handleUserControllerException(UserControllerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка в UserController: " + e.getMessage());
    }

    // Обработчик исключений для UserService
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<String> handleUserServiceException(UserServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка в UserService: " + e.getMessage());
    }
}
