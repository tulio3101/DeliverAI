package edu.eci.ahia.mapper;

import org.mapstruct.Mapper;

import edu.eci.ahia.model.dto.request.OrderItemRequestDTO;
import edu.eci.ahia.model.dto.response.OrderItemResponseDTO;
import edu.eci.ahia.model.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

  OrderItem toEntity(OrderItemRequestDTO dto);

  OrderItemResponseDTO toDto(OrderItem entity);

}
