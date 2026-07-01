package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Request to create an order")
public class OrderRequestDTO {

    @Positive
    @Schema(description = "Order subtotal", example = "150.00")
    private double subTotal;

    @NotEmpty
    @Schema(description = "List of order items")
    private List<OrderItemRequestDTO> orderItems;

}
