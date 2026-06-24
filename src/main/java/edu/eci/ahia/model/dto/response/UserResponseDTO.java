package edu.eci.ahia.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

  private Long id;

  private String name;

  private String email;

  private Long phoneNumber;

  private List<OrderResponseDTO> orders;

}
