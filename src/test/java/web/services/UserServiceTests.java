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
import web.models.Car;
import web.models.Role;
import web.models.User;
import web.models.UserDto;
import web.models.enums.RoleType;
import web.repositories.CarRepository;
import web.repositories.RoleRepository;
import web.repositories.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CarRepository carRepository;
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
        userService = new UserServiceImpl(userRepository, carRepository, roleService, passwordEncoder, initializationService);
    }

    @Test
    void testRegisterUser() {
        UserDto userDto = new UserDto();
        userDto.setLogin("testLogin");
        userDto.setPassword("testPassword");

        Role role = new Role(RoleType.ROLE_ADMIN);
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
        Set<RoleType> roles = new HashSet<>(Collections.singleton(RoleType.ROLE_ADMIN));

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
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
    void testSaveOrUpdateCar() {
        long userId = 1L;
        Car car = new Car();
        car.setBrand("Test Brand");
        car.setSeries("Test Series");
        car.setModel("Test Model");
        car.setColor("Test Color");

        User user = new User();
        user.setCar(car);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.saveOrUpdateCar(userId, car));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetCarByUserId() {
        long userId = 1L;
        User user = new User();
        Car car = new Car();
        car.setBrand("Test Brand");
        car.setSeries("Test Series");
        car.setModel("Test Model");
        car.setColor("Test Color");
        user.setCar(car);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Car result = userService.getCarByUserId(userId);
        assertNotNull(result);
        assertNotNull(result.getBrand());
        assertEquals(car.getBrand(), result.getBrand());
        assertNotNull(result.getSeries());
        assertEquals(car.getSeries(), result.getSeries());
        assertNotNull(result.getModel());
        assertEquals(car.getModel(), result.getModel());
        assertNotNull(result.getColor());
        assertEquals(car.getColor(), result.getColor());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetRoleByUserId() {
        long userId = 1L;
        User user = new User();
        Role role = new Role(RoleType.ROLE_USER);
        Role role2 = new Role(RoleType.ROLE_ADMIN);
        Set<Role> roles = Set.of(role, role2);
        user.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Set<Role> result = userService.getRolesByUserId(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(roles));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetRoleTypeByUserId() {
        long userId = 1L;
        User user = new User();
        Set<RoleType> rolesType = Set.of(RoleType.ROLE_USER, RoleType.ROLE_ADMIN);
        Set<Role> roles = rolesType.stream().map(Role::new).collect(Collectors.toSet());
        user.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Set<Role> result = userService.getRolesByUserId(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(roles));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L); // Установите идентификатор пользователя
        user.setLogin("testLogin");

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user))
                .thenAnswer(invocation -> {
                    User existingUser = invocation.getArgument(0);
                    return Optional.of(cloneUser(existingUser));
                });

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updateUser(user));

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateRole() {
        // Создаем тестовые данные
        long userId = 1L;
        String[] roles = {"ROLE_ADMIN", "ROLE_USER"};

        // Создаем мок-объекты для ролей
        Role adminRole = new Role();
        adminRole.setRoleType(RoleType.ROLE_ADMIN);

        Role userRole = new Role();
        userRole.setRoleType(RoleType.ROLE_USER);

        // Создаем тестового пользователя
        User user = new User();
        user.setId(userId);


        // Настраиваем мок-объекты и поведение userRepository и roleService
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findAll()).thenReturn(Arrays.asList(adminRole, userRole));

        // Вызываем тестируемый метод
        userService.updateRole(userId, roles);

        // Проверяем, что методы userRepository и roleService были корректно вызваны
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        verify(roleRepository).findAll();
        verify(roleRepository).save(adminRole);
        verify(roleRepository).save(userRole);
    }

    @Test
    void testDeleteUserById() {
        long userId = 1L;
        User user = new User();
        Role role = new Role(RoleType.ROLE_USER);
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

    @Test
    void testDeleteCar_WhenUserExistsAndHasCar_ShouldDeleteCar() {
        // Arrange
        User user = new User();
        Car car = new Car();
        user.setCar(car);
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act
        userService.deleteCar(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(carRepository, times(1)).delete(car);
        verify(userRepository, times(1)).save(user);
        assertNull(user.getCar());
    }

    @Test
    void testResetTable_WhenDataAccessExceptionThrown_ShouldThrowUserServiceException() {
        assertDoesNotThrow(() -> userService.resetTable());

        verify(userRepository, times(1)).resetTable();
    }

    @Test
    void testRecreateTable_WhenDataAccessExceptionThrown_ShouldThrowUserServiceException() {
        // Arrange
        doThrow(new RuntimeException("Exception occurred while recreating table"))
                .when(userRepository).createUserRoleTable();

        // Act and Assert
        assertThrows(RuntimeException.class, () -> userService.recreateTable());

        verify(userRepository, times(1)).dropTables();
        verify(userRepository, times(1)).createCarsTable();
        verify(userRepository, times(1)).createRolesTable();
        verify(userRepository, times(1)).createUsersTable();
        verify(userRepository, times(1)).createUserRoleTable();
    }

    private User cloneUser(User user) {
        User clone = new User();
        clone.setId(user.getId());
        clone.setCar(user.getCar());
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