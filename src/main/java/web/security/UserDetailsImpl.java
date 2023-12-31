package web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import web.models.Role;
import web.models.User;

import java.util.Collection;
import java.util.Set;

@Slf4j
public class UserDetailsImpl implements UserDetails {

    private final String login;
    private final String password;
    private final Set<Role> roles;

    public UserDetailsImpl(User user) {
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        log.info("UserDetailsImpl created for user: {}", user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
