package edu.eci.ahia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.ahia.mapper.OrderMapper;
import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.request.OrderRequestDTO;
import edu.eci.ahia.model.dto.response.OrderResponseDTO;
import edu.eci.ahia.model.entity.enums.State;
import edu.eci.ahia.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("")
    public ResponseEntity<OrderResponseDTO> createOrder(
        @Valid @RequestBody OrderRequestDTO dto ){

            if (dto.getOrderItems() != null) {
                for (OrderItemRequestDTO orderItem : dto.getOrderItems()) {
                    orderItem.setOrder(null);
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toDto(orderService.createOrder(orderMapper.toEntity(dto))));
        }
    
    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateState(
        @PathVariable Long id, @RequestParam State state){
            return ResponseEntity.ok(orderMapper.toDto(orderService.updateState(id, state)));
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
        
        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderMapper.toDto(orderService.findOrderById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderMapper.toDtoList(orderService.getAllOrdersByUserId(userId)));
    }

    @GetMapping("/state")
    public ResponseEntity<List<OrderResponseDTO>> findAllOrderByState(@RequestParam State state) {
        return ResponseEntity.ok(orderMapper.toDtoList(orderService.findAllOrderByState(state)));
    }
}