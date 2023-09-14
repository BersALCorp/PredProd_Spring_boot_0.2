package web.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.model.User;
import web.service.UserService;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/")
public class ViewController {

    private final UserService userService;

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
        log.info("ViewController created");
    }

    @GetMapping("user")
    public String showUserTable(Model model) {
        log.info("showUserTable was been called");
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "user_table";
    }

    @GetMapping("admin")
    public String showAdminTable(Model model) {
        log.info("showAdminTable was been called");
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "admin_table";
    }

    @GetMapping("login")
    public String showLoginForm() {
        log.info("showLoginForm was been called");
        return "login";
    }
}
