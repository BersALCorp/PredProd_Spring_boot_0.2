package web.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cars", schema = "public", catalog = "postgres")
@Getter
@Setter
@NoArgsConstructor
public class Car {
    @Transient
    boolean toStringCalled;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("series")
    private String series;
    @JsonProperty("model")
    private String model;
    @JsonProperty("color")
    private String color;

    @JsonProperty("user")
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Car(String brand, String series, String model, String color) {
        this.brand = brand;
        this.series = series;
        this.model = model;
        this.color = color;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append("id=").append(id);
        sb.append(", brand='").append(brand).append('\'');
        sb.append(", series='").append(series).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", color=").append(color);
        if (toStringCalled) {
            toStringCalled = false;
        } else {
            if (user != null) user.toStringCalled = true;
            sb.append(", user=").append(user);
        }
        sb.append('}');
        return sb.toString();
    }
}
