package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.exception.AuthControllerException;
import web.model.RoleEnum;
import web.model.UserDto;
import web.service.UserService;

@Controller
@RequestMapping("/login")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {
        try {
            if (!password.equals(confirmPassword)) {
                return "redirect:/register?error";
            }
        UserDto userDto = createUserDto(username, password);
        userService.registerUser(userDto);
        return "redirect:/login";
        } catch (Exception e) {
            throw new AuthControllerException("Error registering user: " + e);
        }
    }

    private UserDto createUserDto(String username, String password) {
        return new UserDto(username, password, RoleEnum.ROLE_ADMIN);
    }
}
