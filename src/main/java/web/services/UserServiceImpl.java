package web.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.models.Role;
import web.models.User;
import web.models.UserDto;
import web.models.enums.RoleType;
import web.models.enums.SexEnum;
import web.repositories.UserRepository;
import web.services.interfaces.RoleService;
import web.services.interfaces.UserService;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final InitializationService initializationService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder,
                           InitializationService initializationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.initializationService = initializationService;
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {
        User user = userRepository.getByLogin(userDto.getLogin());
        if (user == null) {
            user = new User(
                    "",
                    "",
                    SexEnum.UNDEFINED,
                    0,
                    userDto.getLogin(),
                    passwordEncoder.encode(userDto.getPassword()),
                    ""
            );
            Role roleUser = roleService.getByRoleType(RoleType.USER);
            user.addRole(roleUser);
            roleUser.addUser(user);
            roleService.save(roleUser);
            userRepository.save(user);
        } else {
            throw new EntityExistsException("User with login " + userDto.getLogin() + " already exists");
        }

    }

    @Override
    @Transactional
    public User saveUser(User user, Set<RoleType> rolesType) {
        User userExists = userRepository.getByLogin(user.getLogin());
        if (userExists == null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            rolesType.stream()
                    .map(roleService::getRoleByType)
                    .forEach(role -> {
                        role.addUser(user);
                        roleService.save(role);
                        user.addRole(role);
                    });

            return userRepository.save(user);
        } else {
            throw new EntityExistsException("User with login " + user.getLogin() + " already exists");
        }
    }

    @Override
    public List<User> getAllUsersWithRoles(HttpServletRequest request) {
        String login = request.getUserPrincipal().getName();
        User user = userRepository.getByLogin(login);
        boolean getOne = user.getRoles().stream().allMatch(role ->
                role.getRoleType() == RoleType.USER
        );
        if (getOne) {
            return List.of(user);
        } else {
            return userRepository.findAll(Sort.by("id"));
        }
    }

    @Override
    public User getUsers(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public List<User> getAllUsersSorted() {
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + userId + " not found"));
    }

    @Override
    @Transactional
    public void updateUser(User user, Set<RoleType> roles) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + user.getId()));

        existingUser.getRoles().removeIf(role -> {
            if (!roles.contains(role.getRoleType())) {
                role.getUsers().remove(existingUser);
                roleService.save(role);
                return true;
            } else {
                roles.remove(role.getRoleType());
                return false;
            }
        });

        roles.forEach(roleType -> {
            Role role = roleService.getRoleByType(roleType);
            existingUser.addRole(role);
            role.addUser(existingUser);
            roleService.save(role);
        });

        if (existingUser.getFirstName() != null && !existingUser.getFirstName().equals(user.getFirstName())) existingUser.setFirstName(user.getFirstName());
        if (existingUser.getLastName() != null && !existingUser.getLastName().equals(user.getLastName())) existingUser.setLastName(user.getLastName());
        if (existingUser.getSex() != null && !existingUser.getSex().equals(user.getSex())) existingUser.setSex(user.getSex());
        if (existingUser.getAge() != (user.getAge())) existingUser.setAge(user.getAge());
        if (existingUser.getPassword() != null && !existingUser.getPassword().equals(user.getPassword())) existingUser.setLogin(user.getLogin());
        if (existingUser.getPassword() != null && !existingUser.getPassword().equals(user.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (existingUser.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) existingUser.setEmail(user.getEmail());

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        user.getRoles().forEach(role -> {
            role.getUsers().remove(user);
            roleService.save(role);
        });

        userRepository.deleteById(userId);
    }

    @Override
    public void resetTable() {
        userRepository.resetTable();
        initializationService.initialize();
    }

    @Override
    @Transactional
    public User getUserByLogin(String login) {
        return userRepository.getByLogin(login);
    }

    @Override
    public boolean canEdit(HttpServletRequest request) {
        String login = request.getUserPrincipal().getName();
        User user = userRepository.getByLogin(login);
        return user.getRoles().stream().allMatch(role ->
                role.getRoleType() == RoleType.ADMIN
                        || role.getRoleType() == RoleType.MODERATOR
        );
    }
}