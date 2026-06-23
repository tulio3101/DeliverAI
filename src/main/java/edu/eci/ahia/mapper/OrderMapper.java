package edu.eci.ahia.mapper;

import edu.eci.ahia.model.dto.request.OrderRequestDTO;
import edu.eci.ahia.model.dto.response.OrderResponseDTO;
import edu.eci.ahia.model.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  Order toEntity(OrderRequestDTO dto);
  OrderResponseDTO toDto(Order entity);

}
