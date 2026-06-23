package edu.eci.ahia.mapper;

import edu.eci.ahia.model.dto.request.OrderRequestDTO;
import edu.eci.ahia.model.dto.response.OrderResponseDTO;
import edu.eci.ahia.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderDate", ignore = true)
  @Mapping(target = "state", ignore = true)
  Order toEntity(OrderRequestDTO dto);

  OrderResponseDTO toDto(Order entity);

}
