package web.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.models.Role;
import web.models.enums.RoleType;
import web.repositories.RoleRepository;
import web.services.interfaces.RoleService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByType(RoleType roleType) {
        return roleRepository.getByRoleType(roleType);
    }

    @Override
    public Role getByRoleType(RoleType roleType) {
        return roleRepository.getByRoleType(roleType);
    }

    @Override
    public Optional<Role> findByRoleType(RoleType roleType) { return roleRepository.findByRoleType(roleType); }

    @Override
    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void saveAll(Set<Role> roles) {
        roleRepository.saveAll(roles);
    }
}
