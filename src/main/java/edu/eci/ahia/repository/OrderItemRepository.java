package edu.eci.ahia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.eci.ahia.model.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
