package edu.eci.ahia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.ahia.mapper.OrderItemMapper;
import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.response.OrderItemResponseDTO;
import edu.eci.ahia.service.OrderItemService;
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
@RequestMapping("/order-items")
@RequiredArgsConstructor
@Tag(name = "Order Items", description = "Operations related to order items")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderItemMapper orderItemMapper;

    @PostMapping("")
    @Operation(summary = "Create an order item", description = "Creates a new item associated with an order and product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order item created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderItemResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<OrderItemResponseDTO> createOrderItem(
        @Valid @RequestBody OrderItemRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderItemMapper.toDto(orderItemService.createOrderItem(orderItemMapper.toEntity(dto))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order item", description = "Deletes an existing order item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order item deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Order item not found", content = @Content)
    })
    public ResponseEntity<Void> deleteOrderItem(
        @Parameter(description = "Order item ID", example = "1") @PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
