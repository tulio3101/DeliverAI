package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to create a product")
public class ProductRequestDTO {

    @NotEmpty
    @Schema(description = "Product name", example = "Classic Burger")
    private String name;

    @PositiveOrZero
    @Schema(description = "Stock quantity", example = "50")
    private int units;

    @Positive
    @Schema(description = "Product price", example = "12.99")
    private double price;

}
