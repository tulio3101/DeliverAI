package edu.eci.ahia.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "Request to create or update a user")
public class UserRequestDTO {

  @NotEmpty
  @Schema(description = "User name", example = "John Doe")
  private String name;

  @Email
  @Schema(description = "Email address", example = "john.doe@email.com")
  private String email;

  @Positive
  @Schema(description = "Phone number", example = "3001234567")
  private Long phoneNumber;

  @Schema(description = "List of user orders (automatically assigned)", hidden = true)
  private List<OrderRequestDTO> orders;

}
