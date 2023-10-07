package web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleType implements GrantedAuthority {

    USER("USER"),
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    MODERATOR("MODERATOR");


    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    public static boolean isValidRole(String role) {
        for (RoleType roleType : values()) {
            if (roleType.name().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
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