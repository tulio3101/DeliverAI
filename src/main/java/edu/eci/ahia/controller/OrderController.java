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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operations related to orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("")
    @Operation(summary = "Create an order", description = "Creates a new order with its associated items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
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
    @Operation(summary = "Update order state", description = "Updates the state of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "State updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponseDTO> updateState(
        @Parameter(description = "Order ID", example = "1") @PathVariable Long id,
        @Parameter(description = "New order state", example = "PREPARATION") @RequestParam State state){
            return ResponseEntity.ok(orderMapper.toDto(orderService.updateState(id, state)));
        }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Deletes an existing order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<Void> deleteOrder(
        @Parameter(description = "Order ID", example = "1") @PathVariable Long id){
        
        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns an order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponseDTO> findOrderById(
        @Parameter(description = "Order ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderMapper.toDto(orderService.findOrderById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user", description = "Returns all orders associated with a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of user orders",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByUserId(
        @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(orderMapper.toDtoList(orderService.getAllOrdersByUserId(userId)));
    }

    @GetMapping("/state")
    @Operation(summary = "Get orders by state", description = "Returns all orders matching a specific state")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of orders filtered by state",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class)))
    })
    public ResponseEntity<List<OrderResponseDTO>> findAllOrderByState(
        @Parameter(description = "Order state", example = "IN_CONFIRMATION") @RequestParam State state) {
        return ResponseEntity.ok(orderMapper.toDtoList(orderService.findAllOrderByState(state)));
    }
}
