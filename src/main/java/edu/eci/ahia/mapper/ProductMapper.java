package edu.eci.ahia.mapper;

import edu.eci.ahia.model.dto.request.ProductRequestDTO;
import edu.eci.ahia.model.dto.response.ProductResponseDTO;
import edu.eci.ahia.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(target = "id", ignore=true)
  Product toEntity(ProductRequestDTO dto);
  ProductResponseDTO toDto(Product entity);

}
