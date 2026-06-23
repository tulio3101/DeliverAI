package edu.eci.ahia.model.dto.request;

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
    
    private ProductRequestDTO product;

    private int quantity;

}
