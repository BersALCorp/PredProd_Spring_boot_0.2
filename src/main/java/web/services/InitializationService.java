package web.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import web.models.Role;
import web.models.User;
import web.models.enums.RoleType;
import web.models.enums.SexEnum;
import web.repositories.RoleRepository;
import web.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;

@Service
public class InitializationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public InitializationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initialize() {
        Set<RoleType> rolesType = Set.of(RoleType.ADMIN, RoleType.USER, RoleType.MANAGER, RoleType.MODERATOR);
        rolesType.forEach(this::createRole);

        Set<String> roles = Set.of("admin", "user", "manager");
        roles.forEach(this::createUser);
    }

    private void createUser(String name) {
        Optional<User> existUser = userRepository.findByLogin(name);
        if (existUser.isPresent()) return;

        Role role;
        switch (name) {
            case "admin" -> role = roleRepository.getByRoleType(RoleType.ADMIN);
            case "manager" -> role = roleRepository.getByRoleType(RoleType.MANAGER);
            case "moderator" -> role = roleRepository.getByRoleType(RoleType.MODERATOR);
            case "user" -> role = roleRepository.getByRoleType(RoleType.USER);
            default -> {
                return;
            }
        }

        User user = new User(name, name, SexEnum.UNDEFINED, 0, name, name, name + "@mail.com");

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.addRole(role);
        role.addUser(user);

        roleRepository.save(role);
        userRepository.save(user);
    }

    private void createRole(RoleType roleType) {
        Optional<Role> role = roleRepository.findByRoleType(roleType);
        if (role.isEmpty()) {
            roleRepository.save(new Role(roleType));
        }
    }
}

