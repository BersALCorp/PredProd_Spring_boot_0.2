package web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CarRepository carRepository,
                           CustomPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("UserServiceImpl created");
    }

    @Override
    @Transactional
    public void registerUser(UserDto userDto) {
        logger.info("start registerUser in service");
        User user = new User();
        user.setFirstName("");
        user.setLastName("");
        user.setSex(SexEnum.UNDEFINED);
        user.setAge(0);
        user.setEmail("");
        user.setLogin(userDto.getLogin());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.getRoles().add(userDto.getRole());
        userRepository.save(user);
        logger.info("end registerUser in service");
    }

    @Override
    public User saveUser(User user, RoleEnum role) {
        logger.info("start saveUser in service");
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.getRoles().add(role);
        logger.info("end saveUser in service");
        return userRepository.save(user);
    }
    @Override
    public List<User> findAllSorted() {
        logger.info("done findAllSorted in service");
        return userRepository.findAll(Sort.by("id"));
    }

    @Override
    @Transactional
    public void saveOrUpdateCar(long userId, Car car) {
        logger.info("start saveOrUpdateCar in service");
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
        logger.info("end saveOrUpdateCar in service");
    }

    @Override
    public Car getCarByUserId(long userId) {
        logger.info("start getCarByUserId in service");
        User user = userRepository.findById(userId).orElseThrow();
        Car car = user.getCar();
        if (car == null) {
            car = new Car();
        }
        logger.info("end getCarByUserId in service");
        return car;
    }

    @Override
    public Set<RoleEnum> getRoleByUserId(long userId) {
        logger.info("return getRoleByUserId in service");
        return userRepository.findById(userId).orElseThrow().getRoles();
    }

    @Override
    @Transactional
    public void updateRole(long userId, Set<RoleEnum> roles) {
        logger.info("start updateRole in service");
        Optional<User> existingUser = userRepository.findById(userId);
        existingUser.ifPresent(user -> {
            user.getRoles().clear();
            user.getRoles().addAll(roles);
        });
        logger.info("end updateRole in service");
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        logger.info("start updateUser in service");
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
        logger.info("end updateUser in service");
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
        logger.info("done deleteById in service");
    }

    @Override
    @Transactional
    public void deleteCar(long userId) {
        logger.info("start deleteCar in service");
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Car car = user.getCar();
            if (car != null) {
                user.setCar(null);
                carRepository.delete(car);
                userRepository.save(user);
            }
        }
        logger.info("end deleteCar in service");
    }

    @Override
    public void resetTable() {
        userRepository.resetTable();
        logger.info("done resetTable in service");
    }

    @Override
    @Transactional
    public void recreateTable() {
        userRepository.dropTableUsers();
        userRepository.createCarsTable();
        userRepository.createUsersTable();
        logger.info("done recreateTable in service");
    }
}