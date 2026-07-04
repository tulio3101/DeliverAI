package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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

  @Positive
  @Schema(description = "Existing catalog product ID. Omit it for a custom item (e.g. a personalized cake) — a new product is created automatically from flavor/quantity", example = "1")
  private Long productId;

  @Positive
  @Schema(description = "Product quantity", example = "2")
  private int quantity;

  @NotBlank
  @Schema(description = "Cake flavor", example = "Chocolate")
  private String flavor;

  @NotBlank
  @Schema(description = "Cake filling", example = "Caramel (arequipe)")
  private String filling;

  @Positive
  @Schema(description = "Number of servings the cake yields", example = "10")
  private int servings;

  @NotBlank
  @Schema(description = "Cake decoration / design", example = "Peach gel with a Happy Birthday message")
  private String decoration;

  @Schema(description = "Reference image URL for the cake design, if the customer sent one", example = "https://.../orders/573.../abc.jpg")
  private String referenceImageUrl;

}
