package edu.eci.ahia.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import edu.eci.ahia.model.dto.request.UserRequestDTO;
import edu.eci.ahia.model.dto.response.UserResponseDTO;
import edu.eci.ahia.model.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", uses = OrderMapper.class)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  User toEntity(UserRequestDTO dto);

  UserResponseDTO toDto(User entity);

  List<UserResponseDTO> toDtoList(List<User> userList);

}
