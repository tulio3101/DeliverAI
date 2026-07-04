package edu.eci.ahia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.eci.ahia.exception.OrderNotFoundException;
import edu.eci.ahia.model.entity.Order;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;
  private final ProductService productService;
  private final UserService userService;

  @Transactional
  public Order createOrder(Order entity) {

    User user = userService.findOrCreateByPhoneNumber(entity.getCustomerName(), entity.getPhoneNumber());

    Order newOrder = Order.builder()
        .orderDate(LocalDateTime.now())
        .state(State.IN_CONFIRMATION)
        .subTotal(entity.getSubTotal())
        .orderItems(entity.getOrderItems())
        .user(user)
        .deliveryDate(entity.getDeliveryDate())
        .deliveryAddress(entity.getDeliveryAddress())
        .notes(entity.getNotes())
        .build();

    List<OrderItem> orderItems = newOrder.getOrderItems();
    if (orderItems != null) {
      orderItems.forEach(orderItem -> {
        if (orderItem.getProductId() != null) {
          orderItem.setProduct(productService.findProductById(orderItem.getProductId()));
        } else {
          Product customProduct = productService.createProduct(
              Product.builder()
                  .name("Pastel personalizado - " + orderItem.getFlavor())
                  .units(orderItem.getQuantity())
                  .price(0)
                  .build());
          orderItem.setProduct(customProduct);
        }
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
