package web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import web.exception.AdminControllerException;
import web.exception.AuthControllerException;
import web.exception.LogoutControllerException;
import web.model.*;
import web.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);


    @Autowired
    public ApiController(UserService userService) {
        this.userService = userService;
        logger.info("ApiController created");
    }

    @GetMapping("/getCar/{userId}")
    public ResponseEntity<Map<String, Object>> getCar(@PathVariable("userId") long userId) {
        try {
            logger.info("User with id: {} is trying to get his car.", userId);
            Car car = userService.getCarByUserId(userId);
            if (car == null) {
                return ResponseEntity.ok(responseCreator("Car not found.", null));
            }
            return ResponseEntity.ok(responseCreator("Car retrieved successfully.", car));
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при получении машины.", e);
        }
    }

    @GetMapping("/getRoles/{userId}")
    public ResponseEntity<Map<String, Object>> getRoles(@PathVariable("userId") long userId) {
        try {
            logger.info("User with id: {} is trying to get his roles.", userId);
            Set<RoleEnum> roles = userService.getRoleByUserId(userId);
            if (roles == null) {
                return ResponseEntity.ok(responseCreator("Roles not found.", null));
            }
            return ResponseEntity.ok(responseCreator("Roles retrieved successfully.", roles));
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при получении ролей.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody Map<String, Object> requestData) {
        try {
            Map<String, Object> userMap = (Map<String, Object>) requestData.get("user");
            Map<String, Object> rolesMap = (Map<String, Object>) requestData.get("roles");
            logger.info("User with id: {} is trying to save his user and roles.", userMap.get("id"));
            RoleEnum role = RoleEnum.valueOf(rolesMap.get("roles").toString());
            User user = new User(
                    (String) userMap.get("firstName"),
                    (String) userMap.get("lastName"),
                    SexEnum.valueOf(userMap.get("sex").toString()),
                    Integer.parseInt(userMap.get("age").toString()),
                    (String) userMap.get("login"),
                    (String) userMap.get("password"),
                    (String) userMap.get("email"),
                    role
            );

            User savedUser = userService.saveUser(user, role);

            return ResponseEntity.ok(responseCreator("User saved successfully.", savedUser));
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при сохранении пользователя.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveCar")
    public ResponseEntity<String> saveOrUpdateCar(@RequestParam("userId") long userId, @RequestBody Map<String, Object> requestData) {
        try {
            logger.info("User with id: {} is trying to save his car.", userId);
            String brand = requestData.get("brand").toString();
            String series = requestData.get("series").toString();
            String model = requestData.get("model").toString();
            String color = requestData.get("color").toString();

            Car car = new Car(brand, series, model, color);

            userService.saveOrUpdateCar(userId, car);
            return ResponseEntity.ok("Car saved successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при сохранении машины.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            logger.info("User with id: {} is trying to update his data.", user.getId());
            userService.updateUser(user);
            return ResponseEntity.ok("User id: " + user.getId() + " update successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при обновлении пользователя.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateRoles/{userId}")
    public ResponseEntity<String> updateRoles(@PathVariable("userId") long userId, @RequestBody String[] roles) {
        try {
            logger.info("User with id: {} is trying to update his roles.", userId);
            Set<RoleEnum> rolesEnum = Arrays.stream(roles)
                    .map(RoleEnum::valueOf)
                    .collect(Collectors.toSet());

            userService.updateRole(userId, rolesEnum);

            return ResponseEntity.ok("User roles updated successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при обновлении ролей.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId) {
        try {
            logger.info("User with id: {} is trying to delete his account.", userId);
            userService.deleteById(userId);
            return ResponseEntity.ok("User id: " + userId + " deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при удалении пользователя.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCar/{userId}")
    public ResponseEntity<String> deleteCar(@PathVariable("userId") long userId) {
        try {
            logger.info("User with id: {} is trying to delete his car.", userId);
            userService.deleteCar(userId);
            return ResponseEntity.ok("Car deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при удалении машины.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/resetTable")
    public ResponseEntity<String> resetTable() {
        try {
            logger.info("Trying to reset table.");
            userService.resetTable();
            return ResponseEntity.ok("Reset table successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при очищении таблицы.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/recreateTable")
    public ResponseEntity<String> recreateTable() {
        try {
            logger.info("Trying to recreate table.");
            userService.recreateTable();
            return ResponseEntity.ok("Recreate table successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при пересоздании таблицы.", e);
        }
    }

    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {
        try {
            logger.info("Trying to register user: {}", username);
            if (!password.equals(confirmPassword)) {
                return new RedirectView("/register?error");
            }
            UserDto userDto = createUserDto(username, password);
            userService.registerUser(userDto);
            return new RedirectView("/login");
        } catch (Exception e) {
            throw new AuthControllerException("Error registering user: " + e);
        }
    }

    @PostMapping("/logout")
    public RedirectView logout() {
        try {
            logger.info("Trying to logout user.");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            throw new LogoutControllerException("Error in logging out. " + e);
        }
        return new  RedirectView("/login");
    }

    private Map<String, Object> responseCreator(String message, Object data) {
        logger.info("Creating response: {}", message);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private UserDto createUserDto(String username, String password) {
        logger.info("Creating user: {}", username);
        return new UserDto(username, password, RoleEnum.ROLE_ADMIN);
    }
}
