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

  @Schema(description = "Cake flavor", example = "Chocolate")
  private String flavor;

  @Schema(description = "Cake filling", example = "Caramel (arequipe)")
  private String filling;

  @Schema(description = "Number of servings the cake yields", example = "10")
  private int servings;

  @Schema(description = "Cake decoration / design", example = "Peach gel with a Happy Birthday message")
  private String decoration;

  @Schema(description = "Reference image URL for the cake design")
  private String referenceImageUrl;
}
