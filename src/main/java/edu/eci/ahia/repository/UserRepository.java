package edu.eci.ahia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.eci.ahia.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
