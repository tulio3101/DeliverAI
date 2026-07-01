package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "Request to create an order item")
public class OrderItemRequestDTO {

  @Schema(description = "Order to which the item belongs (automatically assigned)", hidden = true)
  private OrderRequestDTO order;

  @NotNull
  @Positive
  @Schema(description = "Product ID", example = "1")
  private Long productId;

  @Positive
  @Schema(description = "Product quantity", example = "2")
  private int quantity;

}
