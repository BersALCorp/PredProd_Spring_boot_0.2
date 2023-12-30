package web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import web.exceptions.customs.ApiControllerException;
import web.exceptions.customs.LoginControllerException;
import web.models.*;
import web.models.enums.RoleType;
import web.services.interfaces.UserService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
        log.info("ApiController created");
    }

    @GetMapping("/getCar/{userId}")
    public ResponseEntity<Car> getCar(@PathVariable("userId") long userId) {
        try {
            log.info("Trying get car by User with id: {} .", userId);
            Car car = userService.getCarByUserId(userId);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            throw new ApiControllerException("Error when receiving a user car.", e);
        }
    }

    @GetMapping("/getRoles/{userId}")
    public ResponseEntity<Set<RoleType>> getRoles(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to get roles from User with id: {} .", userId);
            Set<RoleType> roles = userService.getRolesTypeByUserId(userId);
            if (roles == null) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            throw new ApiControllerException("Error when getting user roles.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody CombinedData combinedData) {
        try {
            log.info("Trying to save user with data: {} .", combinedData);
            User savedUser = userService.saveUser(
                    combinedData.getUser(),
                    combinedData.getRoles()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            throw new ApiControllerException("An error while saving the user.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveCar")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveOrUpdateCar(@RequestParam("userId") long userId, @RequestBody Car car) {
        try {
            log.info("Trying to save car for user with id: {} .", userId);
            userService.saveOrUpdateCar(userId, car);
        } catch (Exception e) {
            throw new ApiControllerException("An error occurred while saving the car.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUser(@RequestBody User user) {
        try {
            log.info("Trying to update user with id: {} .", user.getId());
            userService.updateUser(user);
        } catch (Exception e) {
            throw new ApiControllerException("An error occurred while updating the user.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateRoles/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateRoles(@PathVariable("userId") long userId, @RequestBody String[] roles) {
        try {
            log.info("Trying to update roles to user with id: {} .", userId);
            userService.updateRole(userId, roles);
        } catch (Exception e) {
            throw new ApiControllerException("An error when updating roles.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to delete user with id: {} .", userId);
            userService.deleteUserById(userId);
        } catch (Exception e) {
            throw new ApiControllerException("Error when removing the user.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCar/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCar(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to delete car with id: {} .", userId);
            userService.deleteCar(userId);
        } catch (Exception e) {
            throw new ApiControllerException("Error when removing the car.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/resetTable")
    @ResponseStatus(HttpStatus.OK)
    public void resetTable() {
        try {
            log.info("Trying to reset table.");
            userService.resetTable();
        } catch (Exception e) {
            throw new ApiControllerException("Error when cleaning the table.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/recreateTable")
    @ResponseStatus(HttpStatus.OK)
    public void recreateTable() {
        try {
            log.info("Trying to recreate table.");
            userService.recreateTable();
        } catch (Exception e) {
            throw new ApiControllerException("Error in the reconstruction of the table.", e);
        }
    }

    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam String regUsername, @RequestParam String regPassword, @RequestParam String confirmPassword) {
        try {
            log.info("Trying to register user: " + regUsername);
            if (!regPassword.equals(confirmPassword)) {
                return new RedirectView("/register?error");
            }
            UserDto userDto = createUserDto(regUsername, regPassword);
            userService.registerUser(userDto);
            return new RedirectView("/login");
        } catch (Exception e) {
            throw new LoginControllerException("Error registering user: " + e);
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
            throw new LoginControllerException("Error in logging out. " + e);
        }
        return new RedirectView("/login");
    }

    private UserDto createUserDto(String username, String password) {
        log.info("Creating user: {}", username);
        return new UserDto(username, password, RoleType.ROLE_ADMIN);
    }

    static class CombinedData {
        private User user;
        private String[] roles;

        CombinedData() {
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Set<RoleType> getRoles() {
            return Arrays.stream(roles).map(RoleType::valueOf).collect(Collectors.toSet());
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }

    }
}