package edu.eci.ahia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.model.dto.request.UserRequestDTO;
import edu.eci.ahia.model.dto.response.UserResponseDTO;
import edu.eci.ahia.mapper.UserMapper;
import edu.eci.ahia.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping("")
  public ResponseEntity<UserResponseDTO> createUser(
      @Valid @RequestBody UserRequestDTO dto) {

    User userToCreate = userService.createUser(userMapper.toEntity(dto));

    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userToCreate));

  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(
      @PathVariable @Valid Long id) {

    return ResponseEntity.ok(userMapper.toDto(userService.getUserById(id)));

  }

  @GetMapping("/all")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsers()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDTO> updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UserRequestDTO dto) {

    User userUpdated = userService.updateUser(id, userMapper.toEntity(dto));

    return ResponseEntity.ok(userMapper.toDto(userUpdated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

    userService.deleteUser(id);

    return ResponseEntity.noContent().build();
  }

}
