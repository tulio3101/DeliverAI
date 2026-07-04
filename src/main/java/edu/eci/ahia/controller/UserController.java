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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping("")
  @Operation(summary = "Create a user", description = "Creates a new user in the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
  })
  public ResponseEntity<UserResponseDTO> createUser(
      @Valid @RequestBody UserRequestDTO dto) {

    User userToCreate = userService.createUser(userMapper.toEntity(dto));

    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userToCreate));

  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Returns a user by their ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<UserResponseDTO> getUserById(
      @Parameter(description = "User ID", example = "1") @PathVariable @Valid Long id) {

    return ResponseEntity.ok(userMapper.toDto(userService.getUserById(id)));

  }

  @GetMapping("/all")
  @Operation(summary = "Get all users", description = "Returns a list of all registered users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "List of users",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
  })
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    return ResponseEntity.ok(userMapper.toDtoList(userService.getAllUsers()));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a user", description = "Updates the data of an existing user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<UserResponseDTO> updateUser(
      @Parameter(description = "User ID", example = "1") @PathVariable Long id,
      @Valid @RequestBody UserRequestDTO dto) {

    User userUpdated = userService.updateUser(id, userMapper.toEntity(dto));

    return ResponseEntity.ok(userMapper.toDto(userUpdated));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a user", description = "Deletes an existing user by their ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID", example = "1") @PathVariable Long id) {

    userService.deleteUser(id);

    return ResponseEntity.noContent().build();
  }

}
