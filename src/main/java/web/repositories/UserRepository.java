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

    @Modifying
    @Query(value =
            "DROP TABLE IF EXISTS users CASCADE;" +
                    "DROP TABLE IF EXISTS roles CASCADE;" +
                    "DROP TABLE IF EXISTS user_role CASCADE;",
            nativeQuery = true)
    void dropTables();

    @Modifying
    @Transactional
    @Query(value =
            "CREATE TABLE IF NOT EXISTS users ("
                    + "id SERIAL PRIMARY KEY,"
                    + "firstName VARCHAR(255) NOT NULL,"
                    + "lastName VARCHAR(255) NOT NULL,"
                    + "age INT NOT NULL,"
                    + "address VARCHAR(255),"
                    + "email VARCHAR(255) NOT NULL,"
                    + "sex VARCHAR(50),"
                    + "role VARCHAR(50),"
                    + "car_id BIGINT REFERENCES cars(id) ON DELETE CASCADE"
                    + ");",
            nativeQuery = true)
    void createUsersTable();

    @Modifying
    @Transactional
    @Query(value =
            "CREATE TABLE IF NOT EXISTS cars ("
                    + "id SERIAL PRIMARY KEY,"
                    + "brand VARCHAR(255) NOT NULL,"
                    + "series VARCHAR(255),"
                    + "model VARCHAR(255),"
                    + "color VARCHAR(255),"
                    + "user_id BIGINT REFERENCES users(id) ON DELETE CASCADE"
                    + ");",
            nativeQuery = true)
    void createCarsTable();

    @Modifying
    @Transactional
    @Query(value =
            "CREATE TABLE IF NOT EXISTS roles ("
                    + "id SERIAL PRIMARY KEY,"
                    + "role_type VARCHAR(255) NOT NULL"
                    + ");",
            nativeQuery = true)
    void createRolesTable();

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS user_role ("
            + "user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,"
            + "role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,"
            + "PRIMARY KEY (user_id, role_id)"
            + ");",
            nativeQuery = true)
    void createUserRoleTable();

    @EntityGraph(value = "User.rolesAndCar")
    User getByLogin(String username);
}
