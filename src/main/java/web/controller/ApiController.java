package web.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
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
import web.exception.UserControllerException;
import web.model.Car;
import web.model.RoleEnum;
import web.model.User;
import web.model.UserDto;
import web.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;

    @Autowired
    public ApiController(UserService userService) {
        this.userService = userService;
        log.info("ApiController created");
    }

    @GetMapping("/getCar/{userId}")
    public ResponseEntity<Map<String, Object>> getCar(@PathVariable("userId") long userId) {
        try {
            log.info("Trying get car by User with id: {} .", userId);
            Car car = userService.getCarByUserId(userId);
            if (car == null) {
                return ResponseEntity.ok(responseCreator("Car not found.", null));
            }
            return ResponseEntity.ok(responseCreator("Car retrieved successfully.", car));
        } catch (Exception e) {
            throw new AdminControllerException("Error when receiving a user car.", e);
        }
    }

    @GetMapping("/getRoles/{userId}")
    public ResponseEntity<Map<String, Object>> getRoles(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to get roles from User with id: {} .", userId);
            Set<RoleEnum> roles = userService.getRoleByUserId(userId);
            if (roles == null) {
                return ResponseEntity.ok(responseCreator("Roles not found.", null));
            }
            return ResponseEntity.ok(responseCreator("Roles retrieved successfully.", roles));
        } catch (Exception e) {
            throw new UserControllerException("Error when getting user roles.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody CombinedData combinedData) {
        try {
            log.info("Trying to save user with data: {} .", combinedData);
            User user = combinedData.getUser();
            RoleEnum role = RoleEnum.valueOf(combinedData.getRole());
            User savedUser = userService.saveUser(user, role);
            return ResponseEntity.ok(responseCreator("User saved successfully.", savedUser));
        } catch (Exception e) {
            throw new AdminControllerException("An error while saving the user.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveCar")
    public ResponseEntity<String> saveOrUpdateCar(@RequestParam("userId") long userId, @RequestBody Car car) {
        try {
            log.info("Trying to save car for user with id: {} .", userId);
            userService.saveOrUpdateCar(userId, car);
            return ResponseEntity.ok("Car saved successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("An error while saving the car.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            log.info("Trying to update user with id: {} .", user.getId());
            userService.updateUser(user);
            return ResponseEntity.ok("User id: " + user.getId() + " update successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("Error when updating the user.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateRoles/{userId}")
    public ResponseEntity<String> updateRoles(@PathVariable("userId") long userId, @RequestBody String[] roles) {
        try {
            log.info("Trying to update roles to user with id: {} .", userId);
            Set<RoleEnum> rolesEnum = Arrays.stream(roles)
                    .map(RoleEnum::valueOf)
                    .collect(Collectors.toSet());

            userService.updateRole(userId, rolesEnum);

            return ResponseEntity.ok("User roles updated successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("An error when updating roles.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to delete user with id: {} .", userId);
            userService.deleteById(userId);
            return ResponseEntity.ok("User id: " + userId + " deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Error when removing the user.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCar/{userId}")
    public ResponseEntity<String> deleteCar(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to delete car with id: {} .", userId);
            userService.deleteCar(userId);
            return ResponseEntity.ok("Car deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Error when removing the car.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/resetTable")
    public ResponseEntity<String> resetTable() {
        try {
            log.info("Trying to reset table.");
            userService.resetTable();
            return ResponseEntity.ok("Reset table successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Error when cleaning the table.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/recreateTable")
    public ResponseEntity<String> recreateTable() {
        try {
            log.info("Trying to recreate table.");
            userService.recreateTable();
            return ResponseEntity.ok("Recreate table successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Error in the reconstruction of the table.", e);
        }
    }

    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {
        try {
            log.info("Trying to register user: " + username);
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
            log.info("Trying to logout user.");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            throw new LogoutControllerException("Error in logging out. " + e);
        }
        return new RedirectView("/login");
    }

    private Map<String, Object> responseCreator(String message, Object data) {
        log.info("Response: {}", message);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private UserDto createUserDto(String username, String password) {
        log.info("Creating user: {}", username);
        return new UserDto(username, password, RoleEnum.ROLE_ADMIN);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class CombinedData {
        private User user;
        private String role;
    }
}