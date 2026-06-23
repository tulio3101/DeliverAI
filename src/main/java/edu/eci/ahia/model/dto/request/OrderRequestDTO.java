package edu.eci.ahia.model.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequestDTO {

    private double subTotal;
    private List<OrderItemRequestDTO> orderItems;

}
