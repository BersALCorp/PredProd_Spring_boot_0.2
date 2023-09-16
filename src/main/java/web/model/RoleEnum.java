package web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.core.GrantedAuthority;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleEnum implements GrantedAuthority {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_GUEST("ROLE_GUEST");


    private final String displayName;

    RoleEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RoleEnum fromValue(String value) {
        for (RoleEnum roleEnum : values()) {
            if (roleEnum.name().equalsIgnoreCase(value)) {
                return roleEnum;
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