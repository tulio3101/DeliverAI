package edu.eci.ahia.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Response with product data")
public class ProductResponseDTO {
  @Schema(description = "Product ID", example = "1")
  private Long id;

  @Schema(description = "Product name", example = "Classic Burger")
  private String name;

  @Schema(description = "Stock quantity", example = "50")
  private int units;

  @Schema(description = "Product price", example = "12.99")
  private double price;

}
