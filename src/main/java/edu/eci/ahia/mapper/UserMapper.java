package edu.eci.ahia.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import edu.eci.ahia.model.dto.request.UserRequestDTO;
import edu.eci.ahia.model.dto.response.UserResponseDTO;
import edu.eci.ahia.model.entity.User;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  User toEntity(UserRequestDTO dto);

  @Mapping(target = "orders", ignore = true)
  UserResponseDTO toDto(User entity);

  List<UserResponseDTO> toDtoList(List<User> userList);

}
