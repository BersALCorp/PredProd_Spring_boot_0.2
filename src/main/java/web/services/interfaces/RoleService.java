package web.services.interfaces;

import web.models.Role;
import web.models.enums.RoleType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {

    Role getRoleByType(RoleType roleType);

    Role getByRoleType(RoleType roleType);

    Optional<Role> findByRoleType(RoleType roleType);

    void save(Role role);

    Role getRoleById(Long id);

    List<Role> getAllRoles();

    void saveAll(Set<Role> roles);

}
