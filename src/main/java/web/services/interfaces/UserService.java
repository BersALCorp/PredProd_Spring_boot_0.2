package web.services.interfaces;

import web.models.*;
import web.models.enums.RoleType;

import java.util.List;
import java.util.Set;

public interface UserService {

    void registerUser(UserDto userDto);

    List<User> getAllUsersSorted();

    void saveOrUpdateCar(long userId, Car car);

    Car getCarByUserId(long userId);

    User getUserById(long userId);

    Set<Role> getRolesByUserId(long userId);

    Set<RoleType> getRolesTypeByUserId(long userId);

    void updateRole(long userId, String[] rolesEnum);

    void updateUser(User user);

    void deleteUserById(long userId);

    void deleteCar(long userId);

    void resetTable();

    void recreateTable();

    User getUserByLogin(String login);

    User saveUser(User user, Set<RoleType> roles);
}
