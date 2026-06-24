package edu.eci.ahia.model.dto.request;

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
public class UserRequestDTO {

  @NotEmpty
  private String name;

  @Email
  private String email;

  @Positive
  private Long phoneNumber;

  private List<OrderRequestDTO> orders;

}
