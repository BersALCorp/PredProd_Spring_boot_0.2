package web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

import javax.persistence.*;

@Entity
@Table(name = "cars", schema = "public", catalog = "postgres")
@Slf4j
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

    public Car() {
    }

    public Car(String brand, String series, String model, String color) {
        this.brand = brand;
        this.series = series;
        this.model = model;
        this.color = color;
        log.info("Car created with brand: {} , series: {} , model: {} , color: {} .", brand, series, model, color);
    }

    @Override
    public String toString() {
        Hibernate.initialize(user);
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

    public boolean isToStringCalled() {
        return toStringCalled;
    }

    public void setToStringCalled(boolean toStringCalled) {
        this.toStringCalled = toStringCalled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
