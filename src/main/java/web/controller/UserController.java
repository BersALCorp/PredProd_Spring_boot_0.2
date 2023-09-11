package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.exception.UserControllerException;
import web.model.Car;
import web.model.RoleEnum;
import web.model.User;
import web.service.UserService;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@PreAuthorize("hasAnyRole('USER')")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @Transactional
    public String showUserTable(Model model) {
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "user_table";
    }

    @PostMapping("/getCar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCar(@RequestParam("userId") long userId) {
        try {
            Car car = userService.getCarByUserId(userId);
            return ResponseEntity.ok(responseCreator("Car retrieved successfully.", car));
        } catch (Exception e) {
            throw new UserControllerException("Error retrieving car.", e);
        }
    }

    @PostMapping("/getRoles")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoles(@RequestParam("userId") long userId) {
        try {
            Set<RoleEnum> roles = userService.getRoleByUserId(userId);
            return ResponseEntity.ok(responseCreator("Roles retrieved successfully.", roles));
        } catch (Exception e) {
            throw new UserControllerException("Error retrieving roles.", e);
        }
    }

    private Map<String, Object> responseCreator(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}
