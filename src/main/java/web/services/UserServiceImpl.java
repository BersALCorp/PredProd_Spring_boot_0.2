package web.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.models.Car;
import web.models.Role;
import web.models.User;
import web.models.UserDto;
import web.models.enums.RoleType;
import web.models.enums.SexEnum;
import web.repositories.CarRepository;
import web.repositories.UserRepository;
import web.services.interfaces.RoleService;
import web.services.interfaces.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final InitializationService initializationService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CarRepository carRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder,
                           InitializationService initializationService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.initializationService = initializationService;
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {
        User user = new User(
                "",
                "",
                SexEnum.UNDEFINED,
                0,
                userDto.getLogin(),
                passwordEncoder.encode(userDto.getPassword()),
                ""
        );
        Role roleUser = roleService.getByRoleType(RoleType.ROLE_USER);
        user.addRole(roleUser);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveUser(User user, Set<RoleType> rolesType) {
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
    }

    @Override
    public List<User> getAllUsersSorted() {
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    @Transactional
    public void saveOrUpdateCar(long userId, Car car) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Car userCar = user.getCar();

        if (userCar == null) {
            userCar = new Car();
        }

        userCar.setBrand(car.getBrand());
        userCar.setSeries(car.getSeries());
        userCar.setModel(car.getModel());
        userCar.setColor(car.getColor());
        user.setCar(userCar);

        userRepository.save(user);
    }

    @Override
    public Car getCarByUserId(long userId) {
        return userRepository.findById(userId)
                .map(User::getCar)
                .orElse(new Car());
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public Set<Role> getRolesByUserId(long userId) {
        return userRepository.findById(userId)
                .map(User::getRoles)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<RoleType> getRolesTypeByUserId(long userId) {
        return userRepository.findById(userId)
                .map(User::getRoles)
                .orElse(Collections.emptySet())
                .stream()
                .map(Role::getRoleType)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void updateRole(long userId, String[] roles) {
        Set<RoleType> rolesType = Arrays.stream(roles).map(RoleType::valueOf).collect(Collectors.toSet());
        boolean userChanged = false;
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        for (Role role : roleService.getAllRoles()) {
            if (!rolesType.contains(role.getRoleType())) {
                if (existingUser.getRoles().contains(role)) {
                    existingUser.getRoles().remove(role);
                    role.getUsers().remove(existingUser);
                    roleService.save(role);
                    userChanged = true;
                    log.info("Role {} was been deleted from user: {}", role.getRoleType(), existingUser.getLogin());
                }
            } else {
                if (!existingUser.getRoles().contains(role)) {
                    role.addUser(existingUser);
                    existingUser.addRole(role);
                    roleService.save(role);
                    userChanged = true;
                    log.info("User {} added to role {}", existingUser.getLogin(), role.getRoleType());
                }
            }
        }

        if (userChanged) {
            userRepository.save(existingUser);
            log.info("User {} updated", existingUser.getLogin());
        } else {
            log.info("User {} not needed update", existingUser.getLogin());
        }
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + user.getId()));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setSex(user.getSex());
        existingUser.setAge(user.getAge());
        existingUser.setLogin(user.getLogin());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setEmail(user.getEmail());

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
    @Transactional
    public void deleteCar(long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            carRepository.delete(u.getCar());
            u.setCar(null);
            userRepository.save(u);
        });
    }

    @Override
    public void resetTable() {
        userRepository.resetTable();
    }

    @Override
    @Transactional
    public void recreateTable() {
        userRepository.dropTables();
        userRepository.createCarsTable();
        userRepository.createRolesTable();
        userRepository.createUsersTable();
        userRepository.createUserRoleTable();
        initializationService.initialize();
    }

    @Override
    @Transactional
    public User getUserByLogin(String login) {
        return userRepository.getByLogin(login);
    }
}