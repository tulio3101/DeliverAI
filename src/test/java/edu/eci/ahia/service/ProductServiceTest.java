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

import edu.eci.ahia.exception.InsufficientStockException;
import edu.eci.ahia.exception.ProductNotFoundException;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_ShouldBuildAndSaveAndReturn() {
        Product input = Product.builder()
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        Product savedProduct = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pizza", result.getName());
        assertEquals(10, result.getUnits());
        assertEquals(25.99, result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProductPrice_WhenProductExists_ShouldUpdatePrice() {
        Product existing = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        Product updated = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(30.00)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Product result = productService.updateProductPrice(1L, 30.00);

        assertEquals(30.00, result.getPrice());
        assertEquals(30.00, existing.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(existing);
    }

    @Test
    void updateProductPrice_WhenProductNotFound_ShouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> productService.updateProductPrice(99L, 30.00));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductUnits_WhenProductExists_ShouldUpdateUnits() {
        Product existing = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        Product result = productService.updateProductUnits(1L, 20);

        assertEquals(20, result.getUnits());
        assertEquals(20, existing.getUnits());
        verify(productRepository).findById(1L);
        verify(productRepository).save(existing);
    }

    @Test
    void updateProductUnits_WhenProductNotFound_ShouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> productService.updateProductUnits(99L, 20));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceUnits_WhenStockSufficient_ShouldReduce() {
        Product product = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.reduceUnits(1L, 3);

        assertEquals(7, product.getUnits());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void reduceUnits_WhenInsufficientStock_ShouldThrow() {
        Product product = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(5)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
            () -> productService.reduceUnits(1L, 10));

        assertTrue(ex.getMessage().contains("Insufficient stock"));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceUnits_WhenQuantityZero_ShouldThrow() {
        Product product = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
            () -> productService.reduceUnits(1L, 0));

        assertTrue(ex.getMessage().contains("greater than zero"));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceUnits_WhenQuantityNegative_ShouldThrow() {
        Product product = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
            () -> productService.reduceUnits(1L, -1));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceUnits_WhenProductNotFound_ShouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> productService.reduceUnits(99L, 3));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDelete() {
        Product product = Product.builder()
            .id(1L)
            .name("Pizza")
            .units(10)
            .price(25.99)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> productService.deleteProduct(99L));
        verify(productRepository).findById(99L);
        verify(productRepository, never()).delete(any());
    }
}
