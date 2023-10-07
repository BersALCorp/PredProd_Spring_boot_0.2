package web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.models.Role;
import web.models.enums.RoleType;
import web.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTests {


    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleService;


    @Test
    void testGetRoleByType() {
        RoleType roleType = RoleType.ADMIN;
        Role expectedRole = new Role();
        expectedRole.setRoleType(roleType);

        when(roleRepository.getByRoleType(roleType)).thenReturn(expectedRole);

        Role result = roleService.getRoleByType(roleType);

        assertEquals(expectedRole, result);
        verify(roleRepository).getByRoleType(roleType);
    }

    @Test
    void testFindByRoleType() {
        RoleType roleType = RoleType.ADMIN;
        Role expectedRole = new Role();
        expectedRole.setRoleType(roleType);

        when(roleRepository.findByRoleType(roleType)).thenReturn(Optional.of(expectedRole));

        Optional<Role> result = roleService.findByRoleType(roleType);

        assertTrue(result.isPresent());
        assertEquals(expectedRole, result.get());
        verify(roleRepository).findByRoleType(roleType);
    }

    @Test
    void testFindByRoleType_roleNotFound() {
        RoleType roleType = RoleType.ADMIN;

        when(roleRepository.findByRoleType(roleType)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findByRoleType(roleType);
        assertTrue(result.isEmpty());

        verify(roleRepository).findByRoleType(roleType);
    }

    @Test
    void testSave() {
        Role role = new Role();

        roleService.save(role);

        verify(roleRepository).save(role);
    }

    @Test
    void testGetRoleById() {
        Long roleId = 1L;
        Role expectedRole = new Role();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(expectedRole));

        Role result = roleService.getRoleById(roleId);

        assertEquals(expectedRole, result);
        verify(roleRepository).findById(roleId);
    }

    @Test
    void testGetRoleById_roleNotPresent() {
        Long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Role result = roleService.getRoleById(roleId);

        assertNull(result);
        verify(roleRepository).findById(roleId);
    }

    @Test
    void testGetAllRoles() {
        List<Role> expectedRoles = List.of(new Role(), new Role(), new Role());

        when(roleRepository.findAll()).thenReturn(expectedRoles);

        List<Role> result = roleService.getAllRoles();

        assertEquals(expectedRoles, result);
        verify(roleRepository).findAll();
    }

    @Test
    void testSaveAll() {
        Role newRole1 = new Role(RoleType.USER);
        Role newRole2 = new Role(RoleType.ADMIN);
        Set<Role> roles = Set.of(newRole1, newRole2);

        roleService.saveAll(roles);

        verify(roleRepository).saveAll(roles);
    }
}
