package web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.exceptions.customs.ViewControllerException;
import web.models.User;
import web.services.interfaces.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class ViewController {

    private final UserService userService;

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
        log.info("ViewController created");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("user")
    public String getCurrentUser(HttpServletRequest request, Model model) {
        try {
            List<User> userList = List.of(
                    userService.getUserByLogin(
                            request.getUserPrincipal().getName()
                    )
            );
            model.addAttribute("userList", userList);
            return "user";
        } catch (Exception e) {
            throw new ViewControllerException("Error when receiving a user.", e);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("manager")
    public String showUserTable(Model model) {
        try {
            log.info("showUserTable was been called");
            List<User> userList = userService.getAllUsersSorted();
            model.addAttribute("userList", userList);
            return "manager_table";
        } catch (Exception e) {
            throw new ViewControllerException("Error when receiving a manager table.", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin")
    public String showAdminTable(Model model) {
        try {
            log.info("showAdminTable was been called");
            List<User> userList = userService.getAllUsersSorted();
            model.addAttribute("userList", userList);
            return "admin_table";
        } catch (Exception e) {
            throw new ViewControllerException("Error when showing the admin table.", e);
        }
    }
}
