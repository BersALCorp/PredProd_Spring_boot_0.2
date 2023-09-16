package web.service;

import web.exception.UserServiceException;
import web.model.Car;
import web.model.RoleEnum;
import web.model.User;
import web.model.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    void registerUser(UserDto userDto) throws UserServiceException;

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
