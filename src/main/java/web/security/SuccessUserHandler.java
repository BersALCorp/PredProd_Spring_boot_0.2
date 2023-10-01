package web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        try {
            if (roles.contains("ROLE_ADMIN")) {
                response.sendRedirect("/admin/");
                log.info("login success as {} with role ADMIN", authentication.getName());
            } else if (roles.contains("ROLE_USER")) {
                response.sendRedirect("/user/");
                log.info("login success as {} with role USER", authentication.getName());
            } else if (roles.contains("ROLE_MANAGER")) {
                response.sendRedirect("/manager/");
                log.info("login success as {} with role MANAGER", authentication.getName());
            }
        } catch (IOException e) {
            log.error("Error redirecting response: {}", e.getMessage());
        }
    }
}