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
import web.models.User;
import web.models.UserDto;
import web.models.enums.RoleType;
import web.services.interfaces.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static web.models.enums.RoleType.isValidRole;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
        log.info("ApiController created");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getCar(HttpServletRequest request) {
        try {
            log.info("Trying to get all users.");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.getAllUsersWithRoles(request));
        } catch (Exception e) {
            throw new ApiControllerException("Error when receiving all users.", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") long userId) {
        try {
            log.info("Trying to get all users.");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.getUsers(userId));
        } catch (Exception e) {
            throw new ApiControllerException("Error when receiving all users.", e);
        }
    }


    @PostMapping("/saveUser")
    @PreAuthorize("hasRole('ADMIN')")
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


    @PutMapping("/updateUser")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUser(@RequestBody CombinedData date) {
        try {
            log.info("Trying to update user with id: {} .", date.getUser().getId());
            userService.updateUser(date.getUser(), date.getRoles());
        } catch (Exception e) {
            throw new ApiControllerException("An error occurred while updating the user.");
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
        return new UserDto(username, password);
    }

    static class CombinedData {
        private User user;
        private String[] roles;

        Set<RoleType> roleSet = new HashSet<>();

        CombinedData() {
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Set<RoleType> getRoles() {
            for (int i = 0; i < roles.length; i++) {
                if (isValidRole(roles[i])) {
                    roleSet.add(RoleType.valueOf(roles[i]));
                }
            }
            return roleSet;
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }

    }
}