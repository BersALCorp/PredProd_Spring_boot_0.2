package web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import web.exception.LogoutControllerException;

@Controller
public class LogoutController {

    @PostMapping("/logout")
    public String logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            throw new LogoutControllerException("Error in logging out. " + e);
        }
        return "redirect:/login";
    }
}
