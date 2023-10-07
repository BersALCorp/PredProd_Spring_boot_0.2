package web.services.interfaces;

import web.models.*;
import web.models.enums.RoleType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserService {

    void registerUser(UserDto userDto);

    User saveUser(User user, Set<RoleType> roles);

    List<User> getAllUsersSorted();

    User getUserById(long userId);

    void updateUser(User user, Set<RoleType> roles);

    void deleteUserById(long userId);

    User getUserByLogin(String login);

    void resetTable();

    boolean canEdit(HttpServletRequest request);

    List<User> getAllUsersWithRoles(HttpServletRequest request);

    User getUsers(long userId);
}
