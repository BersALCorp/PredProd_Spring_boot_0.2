package web.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import web.model.*;
import web.repository.CarRepository;
import web.repository.UserRepository;
import web.security.CustomPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
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
        log.info("UserServiceImpl created");
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {
        log.info("start registerUser in service");
        User user = new User(
                "",
                "",
                SexEnum.UNDEFINED,
                0,
                "",
                userDto.getLogin(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getRole()
        );
        userRepository.save(user);
        log.info("end registerUser in service");
    }

    @Override
    public User saveUser(User user, RoleEnum role) {
        log.info("start saveUser in service");
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.getRoles().add(role);
        log.info("end saveUser in service");
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllSorted() {
        log.info("done findAllSorted in service");
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    @Transactional
    public void saveOrUpdateCar(long userId, Car car) {
        log.info("start saveOrUpdateCar in service");
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
        log.info("end saveOrUpdateCar in service");
    }

    @Override
    public Car getCarByUserId(long userId) {
        log.info("start getCarByUserId in service");
        User user = userRepository.findById(userId).orElseThrow();
        Car car = user.getCar();
        if (car == null) {
            car = new Car();
        }
        log.info("end getCarByUserId in service");
        return car;
    }

    @Override
    public Set<RoleEnum> getRoleByUserId(long userId) {
        log.info("return getRoleByUserId in service");
        return userRepository.findById(userId).orElseThrow().getRoles();
    }

    @Override
    @Transactional
    public void updateRole(long userId, Set<RoleEnum> roles) {
        log.info("start updateRole in service");
        Optional<User> existingUser = userRepository.findById(userId);
        existingUser.ifPresent(user -> {
            user.getRoles().clear();
            user.getRoles().addAll(roles);
        });
        log.info("end updateRole in service");
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        log.info("start updateUser in service");
        userRepository.findById(user.getId()).ifPresent(existingUser -> {
            Car existingCar = existingUser.getCar();
            Set<RoleEnum> existingUserRoles = existingUser.getRoles();
            user.setCar(existingCar);
            user.setRoles(existingUserRoles);
            userRepository.save(user);
            log.info("done updateUser in service");
        });
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
        log.info("done deleteById in service");
    }

    @Override
    @Transactional
    public void deleteCar(long userId) {
        log.info("start deleteCar in service");
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Car car = user.getCar();
            if (car != null) {
                user.setCar(null);
                carRepository.delete(car);
                userRepository.save(user);
            }
        }
        log.info("end deleteCar in service");
    }

    @Override
    public void resetTable() {
        userRepository.resetTable();
        log.info("done resetTable in service");
    }

    @Override
    @Transactional
    public void recreateTable() {
        userRepository.dropTableUsers();
        userRepository.createCarsTable();
        userRepository.createUsersTable();
        log.info("done recreateTable in service");
    }
}