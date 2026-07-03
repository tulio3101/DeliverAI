package edu.eci.ahia.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.eci.ahia.exception.ProductNotFoundException;
import edu.eci.ahia.mapper.ProductMapper;
import edu.eci.ahia.model.dto.request.ProductRequestDTO;
import edu.eci.ahia.model.dto.response.ProductResponseDTO;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    void getAllProducts_ShouldReturn200() throws Exception {
        Product entity = Product.builder().id(1L).name("Pizza").units(10).price(25.99).build();
        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Pizza").units(10).price(25.99).build();

        when(productService.getAllProducts()).thenReturn(java.util.List.of(entity));
        when(productMapper.toDtoList(java.util.List.of(entity)))
            .thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void createProduct_ShouldReturn201() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Pizza", 10, 25.99);
        Product entity = Product.builder().name("Pizza").units(10).price(25.99).build();
        Product saved = Product.builder().id(1L).name("Pizza").units(10).price(25.99).build();
        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Pizza").units(10).price(25.99).build();

        when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(entity);
        when(productService.createProduct(entity)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Pizza\",\"units\":10,\"price\":25.99}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Pizza"))
            .andExpect(jsonPath("$.price").value(25.99));
    }

    @Test
    void createProduct_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateProductPrice_ShouldReturn200() throws Exception {
        Product updated = Product.builder().id(1L).name("Pizza").units(10).price(30.00).build();
        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Pizza").units(10).price(30.00).build();

        when(productService.updateProductPrice(1L, 30.00)).thenReturn(updated);
        when(productMapper.toDto(updated)).thenReturn(response);

        mockMvc.perform(patch("/products/1/price")
                .param("price", "30.00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(30.00));
    }

    @Test
    void updateProductPrice_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(productService.updateProductPrice(anyLong(), anyDouble()))
            .thenThrow(new ProductNotFoundException("not found"));

        mockMvc.perform(patch("/products/99/price")
                .param("price", "30.00"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateProductUnits_ShouldReturn200() throws Exception {
        Product updated = Product.builder().id(1L).name("Pizza").units(20).price(25.99).build();
        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Pizza").units(20).price(25.99).build();

        when(productService.updateProductUnits(1L, 20)).thenReturn(updated);
        when(productMapper.toDto(updated)).thenReturn(response);

        mockMvc.perform(patch("/products/1/units")
                .param("units", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.units").value(20));
    }

    @Test
    void updateProductUnits_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(productService.updateProductUnits(anyLong(), anyInt()))
            .thenThrow(new ProductNotFoundException("not found"));

        mockMvc.perform(patch("/products/99/units")
                .param("units", "20"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/products/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldReturn404() throws Exception {
        doThrow(new ProductNotFoundException("not found"))
            .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/products/99"))
            .andExpect(status().isNotFound());
    }
}
