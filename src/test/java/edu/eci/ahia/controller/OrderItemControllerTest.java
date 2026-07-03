package edu.eci.ahia.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.eci.ahia.exception.OrderItemNotFoundException;
import edu.eci.ahia.mapper.OrderItemMapper;
import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.response.OrderItemResponseDTO;
import edu.eci.ahia.model.entity.OrderItem;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.service.OrderItemService;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private OrderItemMapper orderItemMapper;

    @Test
    void createOrderItem_ShouldReturn201() throws Exception {
        OrderItemRequestDTO request = new OrderItemRequestDTO();
        request.setProductId(1L);
        request.setQuantity(3);
        request.setFlavor("Chocolate");
        request.setFilling("Arequipe");
        request.setServings(10);
        request.setDecoration("Gel de melocoton");

        Product product = Product.builder().id(1L).name("Pizza").units(10).price(25.99).build();
        OrderItem entity = OrderItem.builder().product(product).quantity(3).build();
        OrderItem saved = OrderItem.builder().id(1L).product(product).quantity(3).build();

        OrderItemResponseDTO response = OrderItemResponseDTO.builder()
            .id(1L).quantity(3).build();

        when(orderItemMapper.toEntity(any(OrderItemRequestDTO.class))).thenReturn(entity);
        when(orderItemService.createOrderItem(entity)).thenReturn(saved);
        when(orderItemMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":3,\"flavor\":\"Chocolate\",\"filling\":\"Arequipe\",\"servings\":10,\"decoration\":\"Gel de melocoton\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void createOrderItem_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/order-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrderItem_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/order-items/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrderItem_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new OrderItemNotFoundException("not found"))
            .when(orderItemService).deleteOrderItem(99L);

        mockMvc.perform(delete("/order-items/99"))
            .andExpect(status().isNotFound());
    }
}
