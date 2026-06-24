package edu.eci.ahia.repository;

import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByUserId(Long userId);

  List<Order> findAllByState(State state);

}
