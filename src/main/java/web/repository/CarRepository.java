package web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web.model.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
}
