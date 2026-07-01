package edu.eci.ahia.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response with user data")
public class UserResponseDTO {

  @Schema(description = "User ID", example = "1")
  private Long id;

  @Schema(description = "User name", example = "John Doe")
  private String name;

  @Schema(description = "Email address", example = "john.doe@email.com")
  private String email;

  @Schema(description = "Phone number", example = "3001234567")
  private Long phoneNumber;

  @JsonIgnore
  @Schema(description = "List of user orders")
  private List<OrderResponseDTO> orders;

}
