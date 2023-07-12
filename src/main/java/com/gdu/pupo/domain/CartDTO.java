package com.gdu.pupo.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
  
  private int cartId;
  private String id;
  private LocalDate createdAt;
  private List<CartItemDTO> cartList;
}