package edu.eci.ahia.service;

import org.springframework.stereotype.Service;

import edu.eci.ahia.exception.OrderItemNotFoundException;
import edu.eci.ahia.exception.ProductNotFoundException;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.repository.OrderItemRepository;
import edu.eci.ahia.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public OrderItem createOrderItem(OrderItem entity){
        return orderItemRepository.save(entity);
    }

    public void reduceProductUnits(OrderItem entity) {
        if (entity.getProduct() == null || entity.getProduct().getId() == null) {
            throw new ProductNotFoundException("Product for order item not found");
        }

        productService.reduceUnits(entity.getProduct().getId(), entity.getQuantity());
    }

    public void deleteOrderItem(Long id){
        
        OrderItem orderItemToDelete = orderItemRepository.findById(id)
            .orElseThrow(() -> new OrderItemNotFoundException("Order Item to delete with id: " + id + " not found"));
        
        orderItemRepository.delete(orderItemToDelete);
        
    }

}