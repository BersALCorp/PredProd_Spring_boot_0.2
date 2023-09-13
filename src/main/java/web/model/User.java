package web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "public", catalog = "postgres")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Transient
    boolean toStringCalled;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("age")
    private int age;
    @JsonProperty("login")
    private String login;
    @JsonProperty("password")
    private String password;
    @JsonProperty("email")
    private String email;
    @Enumerated(EnumType.STRING)
    @JsonProperty("sex")
    @JsonDeserialize(using = SexEnumDeserializer.class)
    private SexEnum sex;


    @JsonProperty("roles")
    @JsonDeserialize(using = RoleEnumDeserializer.class)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = RoleEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<RoleEnum> roles = new HashSet<>();

    @JsonProperty("car")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id")
    private Car car;

    private static final Logger logger = LoggerFactory.getLogger(User.class);


    public User(String firstName, String lastName, SexEnum sex, int age, String login, String password, String email, RoleEnum role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.age = age;
        this.login = login;
        this.password = password;
        this.email = email;
        this.roles.add(role);
        logger.info("User created");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", sex='").append(sex).append('\'');
        sb.append(", age='").append(age).append('\'');
        sb.append(", login='").append(login).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", role='").append(roles).append('\'');
        if (toStringCalled) {
            toStringCalled = false;
        } else {
            if (car != null) car.toStringCalled = true;
            sb.append(", car=").append(car);
        }
        sb.append('}');
        return sb.toString();
    }

    public static class SexEnumDeserializer extends JsonDeserializer<SexEnum> {
        @Override
        public SexEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String value = parser.getValueAsString();
            return (value != null) ? SexEnum.fromValue(value) : null;
        }
    }

    public static class RoleEnumDeserializer extends JsonDeserializer<RoleEnum> {
        @Override
        public RoleEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getValueAsString();
            return (value != null) ? RoleEnum.fromValue(value) : null;
        }
    }
}

