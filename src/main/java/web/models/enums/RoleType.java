package web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleType implements GrantedAuthority {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_GUEST("ROLE_GUEST");


    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RoleType fromValue(String value) {
        for (RoleType roleType : values()) {
            if (roleType.name().equalsIgnoreCase(value)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("Invalid value for SexEnum: " + value);
    }

    @Override
    public String getAuthority() {
        return displayName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleEnum{");
        sb.append("displayName='").append(displayName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}