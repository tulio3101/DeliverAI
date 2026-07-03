package edu.eci.ahia.model.entity;

import edu.eci.ahia.model.entity.enums.State;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @PastOrPresent
  @NotNull
  private LocalDateTime orderDate;

  @NotNull
  private State state;

  @NotNull
  @Positive
  private double subTotal;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @NotEmpty
  private List<OrderItem> orderItems;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @NotNull
  @Future
  private LocalDate deliveryDate;

  private String deliveryAddress;

  private String notes;
  
  @Transient
  private String customerName;

  @Transient
  private Long phoneNumber;

}
