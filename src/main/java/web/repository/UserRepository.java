package web.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.model.User;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE users CASCADE", nativeQuery = true)
    void resetTable();

    @Modifying
    @Query(value = "DROP TABLE IF EXISTS users CASCADE", nativeQuery = true)
    void dropTableUsers();

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS users ("
            + "id SERIAL PRIMARY KEY,"
            + "firstName VARCHAR(255) NOT NULL,"
            + "lastName VARCHAR(255) NOT NULL,"
            + "age INT NOT NULL,"
            + "address VARCHAR(255),"
            + "email VARCHAR(255) NOT NULL,"
            + "sex VARCHAR(50),"
            + "role VARCHAR(50),"
            + "car_id BIGINT REFERENCES cars(id) ON DELETE CASCADE"
            + ");", nativeQuery = true)
    void createUsersTable();

    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS cars ("
            + "id SERIAL PRIMARY KEY,"
            + "brand VARCHAR(255) NOT NULL,"
            + "series VARCHAR(255),"
            + "model VARCHAR(255),"
            + "color VARCHAR(255),"
            + "user_id BIGINT REFERENCES users(id) ON DELETE CASCADE"
            + ");", nativeQuery = true)
    void createCarsTable();

    User findByLogin(String login);

    boolean existsByLogin(String login);
}
