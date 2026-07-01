package edu.eci.ahia.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import edu.eci.ahia.model.entity.enums.State;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Response with order data")
public class OrderResponseDTO {
  @Schema(description = "Order ID", example = "1")
  private Long id;

  @Schema(description = "Order date and time", example = "2025-01-15T14:30:00")
  private LocalDateTime orderDate;

  @Schema(description = "Order state", example = "IN_CONFIRMATION")
  private State state;

  @Schema(description = "Order subtotal", example = "150.00")
  private double subTotal;

  @JsonIgnoreProperties("orders")
  @Schema(description = "User associated with the order")
  private UserResponseDTO user;

  @Schema(description = "List of order items")
  private List<OrderItemResponseDTO> orderItems;

}
