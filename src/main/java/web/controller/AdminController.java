package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.exception.AdminControllerException;
import web.model.Car;
import web.model.RoleEnum;
import web.model.SexEnum;
import web.model.User;
import web.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAnyRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showUserTable(Model model) {
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "admin_table";
    }

    @PostMapping("/saveUser")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody Map<String, Object> requestData) {
        try {
            Map<String, Object> userMap = (Map<String, Object>) requestData.get("user");
            Map<String, Object> rolesMap = (Map<String, Object>) requestData.get("roles");
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
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User saved successfully.");
            response.put("user", savedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при сохранении пользователя.", e);
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok("User id: " + user.getId() + " update successfully.");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при обновлении пользователя.", e);
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok("User id: " + userId + " deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при удалении пользователя.", e);
        }
    }

    @PostMapping("/saveCar")
    public ResponseEntity<String> saveOrUpdateCar(@RequestParam("userId") long userId, @RequestBody Map<String, Object> requestData) {
        try {
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

    @GetMapping("/getCar/{userId}")
    public ResponseEntity<Map<String, Object>> getCar(@PathVariable("userId") long userId) {
        try {
            Car car = userService.getCarByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Car retrieved successfully.");
            response.put("car", car);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при получении машины.", e);
        }
    }

    @DeleteMapping("/deleteCar/{userId}")
    public ResponseEntity<String> deleteCar(@PathVariable("userId") long userId) {
        try {
            userService.deleteCar(userId);
            return ResponseEntity.ok("Car deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при удалении машины.", e);
        }
    }

    @GetMapping("/getRoles/{userId}")
    public ResponseEntity<Map<String, Object>> getRoles(@PathVariable("userId") long userId) {
        try {
            Set<RoleEnum> roles = userService.getRoleByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Roles retrieved successfully.");
            response.put("roles", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при получении ролей.", e);
        }
    }

    @PostMapping("/updateRoles/{userId}")
    public ResponseEntity<Map<String, String>> updateRoles(@PathVariable("userId") long userId, @RequestBody String[] roles) {
        try {
            Set<RoleEnum> rolesEnum = Arrays.stream(roles)
                    .map(RoleEnum::valueOf)
                    .collect(Collectors.toSet());

            userService.updateRole(userId, rolesEnum);

            return ResponseEntity.ok(Map.of("message", "User roles updated successfully."));
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при обновлении ролей.", e);
        }
    }

    @DeleteMapping("/resetTable")
    public ResponseEntity<String> resetTable() {
        try {
            userService.resetTable();
            return ResponseEntity.ok(" deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при очищении таблицы.", e);
        }
    }

    @DeleteMapping("/recreateTable")
    public ResponseEntity<String> recreateTable() {
        try {
            userService.recreateTable();
            return ResponseEntity.ok(" deleted successfully");
        } catch (Exception e) {
            throw new AdminControllerException("Ошибка при пересоздании таблицы.", e);
        }
    }
}