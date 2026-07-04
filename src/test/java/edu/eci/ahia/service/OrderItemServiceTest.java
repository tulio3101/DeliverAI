package edu.eci.ahia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.ahia.exception.OrderItemNotFoundException;
import edu.eci.ahia.exception.ProductNotFoundException;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.repository.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void createOrderItem_ShouldSaveAndReturn() {
        OrderItem input = OrderItem.builder()
            .quantity(5)
            .build();

        OrderItem saved = OrderItem.builder()
            .id(1L)
            .quantity(5)
            .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(saved);

        OrderItem result = orderItemService.createOrderItem(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getQuantity());
        verify(orderItemRepository).save(input);
    }

    @Test
    void reduceProductUnits_WithValidProduct_ShouldDelegateToProductService() {
        Product product = Product.builder().id(1L).build();
        OrderItem item = OrderItem.builder()
            .product(product)
            .quantity(3)
            .build();

        orderItemService.reduceProductUnits(item);

        verify(productService).reduceUnits(1L, 3);
    }

    @Test
    void reduceProductUnits_WhenProductIsNull_ShouldThrow() {
        OrderItem item = OrderItem.builder()
            .product(null)
            .quantity(3)
            .build();

        assertThrows(ProductNotFoundException.class,
            () -> orderItemService.reduceProductUnits(item));
        verify(productService, never()).reduceUnits(any(), anyInt());
    }

    @Test
    void reduceProductUnits_WhenProductIdIsNull_ShouldThrow() {
        Product product = Product.builder().id(null).build();
        OrderItem item = OrderItem.builder()
            .product(product)
            .quantity(3)
            .build();

        assertThrows(ProductNotFoundException.class,
            () -> orderItemService.reduceProductUnits(item));
        verify(productService, never()).reduceUnits(any(), anyInt());
    }

    @Test
    void deleteOrderItem_WhenExists_ShouldDelete() {
        OrderItem item = OrderItem.builder()
            .id(1L)
            .quantity(5)
            .build();

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));

        orderItemService.deleteOrderItem(1L);

        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).delete(item);
    }

    @Test
    void deleteOrderItem_WhenNotFound_ShouldThrow() {
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderItemNotFoundException.class,
            () -> orderItemService.deleteOrderItem(99L));
        verify(orderItemRepository).findById(99L);
        verify(orderItemRepository, never()).delete(any());
    }
}
