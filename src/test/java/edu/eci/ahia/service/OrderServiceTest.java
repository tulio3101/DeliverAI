package edu.eci.ahia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.ahia.exception.OrderNotFoundException;
import edu.eci.ahia.model.entity.Order;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.repository.OrderRepository;
import edu.eci.ahia.service.ProductService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void createOrder_WithOrderItems_ShouldCreateOrderAndReduceUnits() {
        Product product = Product.builder().id(1L).build();
        OrderItem item1 = OrderItem.builder().product(product).productId(1L).quantity(3).build();
        OrderItem item2 = OrderItem.builder().product(product).productId(1L).quantity(2).build();
        User user = User.builder().id(1L).name("Juliana").phoneNumber(573187063281L).build();

        Order input = Order.builder()
            .subTotal(100.0)
            .orderItems(List.of(item1, item2))
            .customerName("Juliana")
            .phoneNumber(573187063281L)
            .build();

        Order savedOrder = Order.builder()
            .id(1L)
            .orderDate(java.time.LocalDateTime.now())
            .state(State.IN_CONFIRMATION)
            .subTotal(100.0)
            .orderItems(List.of(item1, item2))
            .user(user)
            .build();

        when(userService.findOrCreateByPhoneNumber("Juliana", 573187063281L)).thenReturn(user);
        when(productService.findProductById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(State.IN_CONFIRMATION, result.getState());
        assertEquals(100.0, result.getSubTotal());

        verify(orderRepository).save(orderCaptor.capture());
        Order captured = orderCaptor.getValue();
        assertEquals(State.IN_CONFIRMATION, captured.getState());
        assertEquals(100.0, captured.getSubTotal());
        assertNotNull(captured.getOrderDate());
        assertEquals(user, captured.getUser());

        assertEquals(item1, captured.getOrderItems().get(0));
        assertEquals(item2, captured.getOrderItems().get(1));

        verify(orderItemService).reduceProductUnits(item1);
        verify(orderItemService).reduceProductUnits(item2);

        assertSame(captured, item1.getOrder());
        assertSame(captured, item2.getOrder());
    }

    @Test
    void createOrder_WithoutProductId_ShouldCreateCustomProductFromCakeFields() {
        OrderItem item = OrderItem.builder().flavor("Chocolate").quantity(1).build();
        User user = User.builder().id(2L).name("Camila").phoneNumber(573000000000L).build();
        Product customProduct = Product.builder().id(5L).name("Pastel personalizado - Chocolate").units(1).price(0).build();

        Order input = Order.builder()
            .subTotal(0.0)
            .orderItems(List.of(item))
            .customerName("Camila")
            .phoneNumber(573000000000L)
            .build();

        Order savedOrder = Order.builder()
            .id(4L)
            .state(State.IN_CONFIRMATION)
            .orderItems(List.of(item))
            .user(user)
            .build();

        when(userService.findOrCreateByPhoneNumber("Camila", 573000000000L)).thenReturn(user);
        when(productService.createProduct(any(Product.class))).thenReturn(customProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        orderService.createOrder(input);

        verify(productService).createProduct(argThat(p ->
            "Pastel personalizado - Chocolate".equals(p.getName()) && p.getUnits() == 1));
        verify(productService, never()).findProductById(any());
        assertEquals(customProduct, item.getProduct());
    }

    @Test
    void createOrder_WithNullOrderItems_ShouldCreateOrderWithoutReducing() {
        Order input = Order.builder()
            .subTotal(50.0)
            .orderItems(null)
            .build();

        Order savedOrder = Order.builder()
            .id(2L)
            .orderDate(java.time.LocalDateTime.now())
            .state(State.IN_CONFIRMATION)
            .subTotal(50.0)
            .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(input);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(State.IN_CONFIRMATION, result.getState());
        assertEquals(50.0, result.getSubTotal());

        verify(orderItemService, never()).reduceProductUnits(any());
    }

    @Test
    void createOrder_WithEmptyOrderItems_ShouldCreateOrderWithoutReducing() {
        Order input = Order.builder()
            .subTotal(30.0)
            .orderItems(List.of())
            .build();

        Order savedOrder = Order.builder()
            .id(3L)
            .orderDate(java.time.LocalDateTime.now())
            .state(State.IN_CONFIRMATION)
            .subTotal(30.0)
            .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(input);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        verify(orderItemService, never()).reduceProductUnits(any());
    }

    @Test
    void updateState_WhenOrderExists_ShouldUpdateState() {
        Order existing = Order.builder()
            .id(1L)
            .state(State.IN_CONFIRMATION)
            .subTotal(100.0)
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenReturn(existing);

        Order result = orderService.updateState(1L, State.PREPARATION);

        assertEquals(State.PREPARATION, result.getState());
        assertEquals(State.PREPARATION, existing.getState());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(existing);
    }

    @Test
    void updateState_WhenOrderNotFound_ShouldThrow() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
            () -> orderService.updateState(99L, State.PREPARATION));
        verify(orderRepository).findById(99L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_WhenOrderExists_ShouldDelete() {
        Order existing = Order.builder()
            .id(1L)
            .state(State.IN_CONFIRMATION)
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));

        orderService.deleteOrder(1L);

        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(existing);
    }

    @Test
    void deleteOrder_WhenOrderNotFound_ShouldThrow() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
            () -> orderService.deleteOrder(99L));
        verify(orderRepository).findById(99L);
        verify(orderRepository, never()).delete(any());
    }
}
