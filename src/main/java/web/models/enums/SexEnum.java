package web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SexEnum {

    UNDEFINED("UNDEFINED"),
    MALE("MALE"),
    FEMALE("FEMALE");


    private final String displayName;

    SexEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static SexEnum fromValue(String value) {
        for (SexEnum sex : values()) {
            if (sex.name().equalsIgnoreCase(value)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Invalid value for SexEnum: " + value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SexEnum{");
        sb.append("displayName='").append(displayName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}