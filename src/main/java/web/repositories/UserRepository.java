package web.repositories;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.models.User;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "User.rolesAndCar")
    Optional<User> findByLogin(String login);

    @EntityGraph(value = "User.rolesAndCar")
    Optional<User> findById(long id);

    @EntityGraph(value = "User.rolesAndCar")
    User getById(long id);

    @Modifying
    @Transactional
    @Query(value =
            "TRUNCATE TABLE users CASCADE;" +
            "TRUNCATE TABLE roles CASCADE;",
            nativeQuery = true)
    void resetTable();

    @EntityGraph(value = "User.rolesAndCar")
    User getByLogin(String username);
}
