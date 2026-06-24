package edu.eci.ahia.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDTO {

    @Positive
    private double subTotal;

    @NotEmpty
    private List<OrderItemRequestDTO> orderItems;

}
