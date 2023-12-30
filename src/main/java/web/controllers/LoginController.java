package web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import web.exceptions.customs.LoginControllerException;

import java.util.Base64;


@Controller
@RequestMapping("/")
@Slf4j
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("login")
    public String showLoginForm() {
        try {
            log.info("showLoginForm was been called");
            return "login";
        } catch (Exception e) {
            throw new LoginControllerException("Error when showing the login form.", e);
        }
    }

    @PostMapping("login")
    public ResponseEntity<Authentication> login(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(6);
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedToken = new String(decodedBytes);

            String[] credentials = decodedToken.split(":");
            String username = credentials[0];
            String password = credentials[1];

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            return ResponseEntity.ok(authentication);
        } catch (Exception e) {
            throw new LoginControllerException("Error when logging in.", e);
        }
    }
}
