package web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import web.models.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
}
