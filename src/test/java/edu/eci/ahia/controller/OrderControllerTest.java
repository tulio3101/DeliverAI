package edu.eci.ahia.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.eci.ahia.exception.OrderNotFoundException;
import edu.eci.ahia.mapper.OrderMapper;
import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.request.OrderRequestDTO;
import edu.eci.ahia.model.dto.request.ProductRequestDTO;
import edu.eci.ahia.model.dto.response.OrderItemResponseDTO;
import edu.eci.ahia.model.dto.response.OrderResponseDTO;
import edu.eci.ahia.model.entity.Order;
import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.service.OrderService;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    void createOrder_ShouldReturn201() throws Exception {
        ProductRequestDTO productRequest = new ProductRequestDTO("Pizza", 10, 25.99);
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setProduct(productRequest);
        itemRequest.setQuantity(3);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setSubTotal(77.97);
        request.setOrderItems(List.of(itemRequest));

        Order entity = Order.builder().subTotal(77.97).build();
        Order saved = Order.builder()
            .id(1L)
            .subTotal(77.97)
            .state(State.IN_CONFIRMATION)
            .build();

        OrderResponseDTO response = OrderResponseDTO.builder()
            .id(1L)
            .subTotal(77.97)
            .state(State.IN_CONFIRMATION)
            .build();

        when(orderMapper.toEntity(any(OrderRequestDTO.class))).thenReturn(entity);
        when(orderService.createOrder(entity)).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subTotal\":77.97,\"orderItems\":[{\"product\":{\"name\":\"Pizza\",\"units\":10,\"price\":25.99},\"quantity\":3}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.subTotal").value(77.97));
    }

    @Test
    void createOrder_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateState_ShouldReturn200() throws Exception {
        Order updated = Order.builder()
            .id(1L)
            .subTotal(100.0)
            .state(State.PREPARATION)
            .build();

        OrderResponseDTO response = OrderResponseDTO.builder()
            .id(1L)
            .subTotal(100.0)
            .state(State.PREPARATION)
            .build();

        when(orderService.updateState(1L, State.PREPARATION)).thenReturn(updated);
        when(orderMapper.toDto(updated)).thenReturn(response);

        mockMvc.perform(patch("/order/1")
                .param("state", "PREPARATION"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value("PREPARATION"));
    }

    @Test
    void updateState_WhenOrderNotFound_ShouldReturn500() throws Exception {
        when(orderService.updateState(anyLong(), any(State.class)))
            .thenThrow(new OrderNotFoundException("not found"));

        mockMvc.perform(patch("/order/99")
                .param("state", "PREPARATION"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteOrder_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/order/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_WhenOrderNotFound_ShouldReturn500() throws Exception {
        doThrow(new OrderNotFoundException("not found"))
            .when(orderService).deleteOrder(99L);

        mockMvc.perform(delete("/order/99"))
            .andExpect(status().isInternalServerError());
    }
}
