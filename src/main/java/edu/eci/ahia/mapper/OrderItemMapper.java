package edu.eci.ahia.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.response.OrderItemResponseDTO;
import edu.eci.ahia.model.entity.OrderItem;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderItemMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "product", ignore = true)
  OrderItem toEntity(OrderItemRequestDTO dto);

  @Mapping(target = "order", ignore = true)
  OrderItemResponseDTO toDto(OrderItem entity);

}
