package web.service;

import web.model.Car;
import web.model.RoleEnum;
import web.model.User;
import web.model.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

//    boolean login(String login, String password);

    void registerUser(UserDto userDto);

    List<User> findAllSorted();

    void saveOrUpdateCar(long userId, Car car);

    Car getCarByUserId(long userId);

    Set<RoleEnum> getRoleByUserId(long userId);

    void updateRole(long userId, Set<RoleEnum> rolesEnum);

    void updateUser(User user);

    void deleteById(long userId);

    void deleteCar(long userId);

    void resetTable();

    void recreateTable();

    User saveUser(User user, RoleEnum roles);
}
