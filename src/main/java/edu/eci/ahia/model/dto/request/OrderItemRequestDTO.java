package edu.eci.ahia.model.dto.request;

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
public class OrderItemRequestDTO {

  private OrderRequestDTO order;

  @NotNull
  @Positive
  private Long productId;

  @Positive
  private int quantity;

}
