package web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.models.Role;
import web.models.User;
import web.models.UserDto;
import web.models.enums.RoleType;
import web.repositories.RoleRepository;
import web.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private InitializationService initializationService;
    @InjectMocks
    private RoleServiceImpl roleService;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        roleService = new RoleServiceImpl(roleRepository);
        userService = new UserServiceImpl(userRepository, roleService, passwordEncoder, initializationService);
    }

    @Test
    void testRegisterUser() {
        UserDto userDto = new UserDto();
        userDto.setLogin("testLogin");
        userDto.setPassword("testPassword");

        Role role = new Role(RoleType.ADMIN);
        Set<Role> roles = new HashSet<>(Collections.singleton(role));
        User user1 = new User();
        user1.setRoles(roles);

        when(roleService.getByRoleType(any(RoleType.class))).thenReturn(role);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user1);

        assertDoesNotThrow(() -> userService.registerUser(userDto));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setPassword("testPassword");
        Set<RoleType> roles = new HashSet<>(Collections.singleton(RoleType.ADMIN));

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        Role role = new Role();
        role.setRoleType(RoleType.ADMIN);
        when(roleRepository.getByRoleType(any(RoleType.class))).thenReturn(role);

        assertDoesNotThrow(() -> userService.saveUser(user, roles));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUsersSorted() {
        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll(any(Sort.class))).thenReturn(userList);

        List<User> result = userService.getAllUsersSorted();

        assertNotNull(result);
        assertEquals(userList.size(), result.size());
        verify(userRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L); // Установите идентификатор пользователя
        user.setLogin("testLogin");
        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.ADMIN);
        roles.add(RoleType.USER);
        Role userRole = new Role(RoleType.USER);
        Role adminRole = new Role(RoleType.ADMIN);
        user.addRole(adminRole);
        user.addRole(userRole);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user))
                .thenAnswer(invocation -> {
                    User existingUser = invocation.getArgument(0);
                    return Optional.of(cloneUser(existingUser));
                });
        when(roleService.getByRoleType(RoleType.ADMIN)).thenReturn(adminRole);
        when(roleService.getByRoleType(RoleType.USER)).thenReturn(userRole);

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);


        assertDoesNotThrow(() -> userService.updateUser(user, roles));

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testDeleteUserById() {
        long userId = 1L;
        User user = new User();
        Role role = new Role(RoleType.USER);
        user.setId(userId);
        user.addRole(role);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByLogin() {
        String login = "login";
        User user = new User();
        user.setLogin(login);

        when(userRepository.getByLogin(login)).thenReturn(user);

        User result = userService.getUserByLogin(login);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).getByLogin(login);
    }

    private User cloneUser(User user) {
        User clone = new User();
        clone.setId(user.getId());
        clone.setRoles(user.getRoles());
        clone.setFirstName(user.getFirstName());
        clone.setLastName(user.getLastName());
        clone.setSex(user.getSex());
        clone.setAge(user.getAge());
        clone.setLogin(user.getLogin());
        clone.setPassword(user.getPassword());
        clone.setEmail(user.getEmail());
        return clone;
    }

}