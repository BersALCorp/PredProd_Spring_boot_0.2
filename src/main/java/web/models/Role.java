package web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.slf4j.Slf4j;
import web.models.enums.RoleType;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Entity
@Table(name = "roles")
@NamedEntityGraph(name = "Role.users", attributeNodes = @NamedAttributeNode("users"))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = RoleTypeDeserializer.class)
    private RoleType roleType;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role(RoleType roleType) {
        this();
        this.roleType = roleType;
    }

    public Role() {
    }

    public void addUser(User user) {
        if (users.contains(user)) {
            log.info("User {} already exists this role", user.getLogin());
            return;
        }
        log.info("Adding user {} to role {}", user.getLogin(), roleType);
        users.add(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return roleType.toString().equals(role.roleType.toString());
    }

    @Override
    public int hashCode() {
        return roleType.hashCode();
    }

    public static class RoleTypeDeserializer extends JsonDeserializer<RoleType> {
        @Override
        public RoleType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getValueAsString();
            return (value != null) ? RoleType.fromValue(value) : null;
        }
    }
}
