package edu.eci.ahia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.eci.ahia.exception.OrderNotFoundException;
import edu.eci.ahia.model.entity.Order;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;

  @Transactional
  public Order createOrder(Order entity) {

    Order newOrder = Order.builder()
        .orderDate(LocalDateTime.now())
        .state(State.IN_CONFIRMATION)
        .subTotal(entity.getSubTotal())
        .orderItems(entity.getOrderItems())
        .user(entity.getUser())
        .build();

    List<OrderItem> orderItems = newOrder.getOrderItems();
    if (orderItems != null) {
      orderItems.forEach(orderItem -> {
        orderItem.setOrder(newOrder);
        orderItemService.reduceProductUnits(orderItem);
      });
    }

    Order orderToSave = orderRepository.save(newOrder);

    return orderToSave;

  }

  @Transactional
  public Order updateState(Long id, State state) {
    Order orderToUpdate = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order to change state with id: " + id + " not found"));

    orderToUpdate.setState(state);

    Order orderToSave = orderRepository.save(orderToUpdate);

    return orderToSave;

  }

  @Transactional
  public void deleteOrder(Long id) {
    Order orderToDelete = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order to delete with id: " + id + " not found"));

    orderRepository.delete(orderToDelete);

  }

  @Transactional
  public List<Order> getAllOrdersByUserId(Long userId) {

    return orderRepository.findByUserId(userId);
  }

  @Transactional
  public List<Order> findAllOrderByState(State state) {
    return orderRepository.findAllByState(state);
  }

  @Transactional
  public Order findOrderById(Long id) {

    Order orderToReturn = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order to Get with id: " + id + "not found"));

    return orderToReturn;
  }

}
