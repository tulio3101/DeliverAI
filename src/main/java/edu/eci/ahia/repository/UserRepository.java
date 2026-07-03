package edu.eci.ahia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.eci.ahia.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(Long phoneNumber);

}
