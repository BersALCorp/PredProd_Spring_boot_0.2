package web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import web.model.Car;
import web.model.RoleEnum;
import web.model.User;
import web.model.UserDto;
import web.repository.CarRepository;
import web.repository.UserRepository;
import web.security.CustomPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CustomPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CarRepository carRepository,
                           CustomPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(UserDto userDto) {
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.getRoles().add(userDto.getRole());
        userRepository.save(user);
    }

    @Override
    public User saveUser(User user, RoleEnum role) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.getRoles().add(role);
        return userRepository.save(user);
    }
    @Override
    public List<User> findAllSorted() {
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    @Transactional
    public void saveOrUpdateCar(long userId, Car car) {
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            Car userCar = user.getCar();
            if (userCar == null) {
                user.setCar(car);
            } else {
                userCar.setBrand(car.getBrand());
                userCar.setSeries(car.getSeries());
                userCar.setModel(car.getModel());
                userCar.setColor(car.getColor());
            }
            userRepository.save(user);
        });
    }

    @Override
    public Car getCarByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Car car = user.getCar();
        if (car == null) {
            car = new Car();
        }
        return car;
    }

    @Override
    public Set<RoleEnum> getRoleByUserId(long userId) {
        return userRepository.findById(userId).orElseThrow().getRoles();
    }

    @Override
    @Transactional
    public void updateRole(long userId, Set<RoleEnum> roles) {
        Optional<User> existingUser = userRepository.findById(userId);
        existingUser.ifPresent(user -> {
            user.getRoles().clear();
            user.getRoles().addAll(roles);
        });
    }

    @Override
    public void updateUser(User user) {
        userRepository.findById(user.getId()).ifPresent(existingUser -> {
            Car existingCar = existingUser.getCar();
            Set<RoleEnum> existingUserRoles = existingUser.getRoles();

            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setAge(user.getAge());
            existingUser.setLogin(user.getLogin());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setSex(user.getSex());
            existingUser.setRoles(existingUserRoles);
            existingUser.setCar(existingCar);
        });
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void deleteCar(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Car car = user.getCar();
            if (car != null) {
                user.setCar(null);
                carRepository.delete(car);
                userRepository.save(user);
            }
        }
    }

    @Override
    public void resetTable() {
        userRepository.resetTable();
    }

    @Override
    @Transactional
    public void recreateTable() {
        userRepository.dropTableUsers();
        userRepository.createCarsTable();
        userRepository.createUsersTable();
    }
}