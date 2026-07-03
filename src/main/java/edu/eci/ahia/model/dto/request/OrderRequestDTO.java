package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
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
    @Valid
    @Schema(description = "List of order items")
    private List<OrderItemRequestDTO> orderItems;

    @NotBlank
    @Schema(description = "Customer name", example = "Juliana Perez")
    private String customerName;

    @NotNull
    @Positive
    @Schema(description = "Customer WhatsApp phone number, used to find or create the associated user", example = "573187063281")
    private Long phoneNumber;

    @NotNull
    @Future
    @Schema(description = "Delivery date (at least 48h in advance)", example = "2026-07-05")
    private LocalDate deliveryDate;

    @Schema(description = "Delivery address; empty if picked up in store")
    private String deliveryAddress;

    @Schema(description = "Additional notes for the order (allergies, message, etc.)")
    private String notes;

}
