package edu.eci.ahia.model.dto.request;

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

  private String name;

  private String email;

  private Long phoneNumber;

  private List<OrderRequestDTO> orders;

}
