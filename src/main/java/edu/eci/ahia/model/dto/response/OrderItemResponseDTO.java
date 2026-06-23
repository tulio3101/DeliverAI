package edu.eci.ahia.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderItemResponseDTO {
  private Long id;

  private OrderResponseDTO order;

  private ProductResponseDTO product;

  private int quantity;
}
