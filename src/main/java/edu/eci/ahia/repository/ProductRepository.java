package edu.eci.ahia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.eci.ahia.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
