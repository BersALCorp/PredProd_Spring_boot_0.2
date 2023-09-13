package web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.model.User;
import web.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/")
public class ViewController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
        logger.info("ViewController created");
    }

    @GetMapping("user")
    public String showUserTable(Model model) {
        logger.info("showUserTable was been called");
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "user_table";
    }

    @GetMapping("admin")
    public String showAdminTable(Model model) {
        logger.info("showAdminTable was been called");
        List<User> userList = userService.findAllSorted();
        model.addAttribute("userList", userList);
        return "admin_table";
    }

    @GetMapping("login")
    public String showLoginForm() {
        logger.info("showLoginForm was been called");
        return "login";
    }
}
