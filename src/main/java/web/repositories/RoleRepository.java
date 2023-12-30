package web.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.models.Role;
import web.models.enums.RoleType;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @EntityGraph(value = "Role.users")
    Role getByRoleType(RoleType roleType);

    @EntityGraph(value = "Role.users")
    Optional<Role> findByRoleType(RoleType roleType);

}
