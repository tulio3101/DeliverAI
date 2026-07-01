package edu.eci.ahia.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Response with order item data")
public class OrderItemResponseDTO {
  @Schema(description = "Item ID", example = "1")
  private Long id;

  @JsonIgnore
  @Schema(description = "Order it belongs to")
  private OrderResponseDTO order;

  @Schema(description = "Associated product")
  private ProductResponseDTO product;

  @Schema(description = "Product quantity", example = "2")
  private int quantity;
}
