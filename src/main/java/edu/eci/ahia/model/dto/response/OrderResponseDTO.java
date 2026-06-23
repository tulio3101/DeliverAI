package edu.eci.ahia.model.dto.response;

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
public class OrderResponseDTO {
  private Long id;

  private LocalDateTime orderDate;

  private State state;

  private double subTotal;

  private List<OrderItemResponseDTO> orderItems;

}
