package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
  private int cartItemId;
  private ItemDTO itemDTO;
  private UserDTO userDTO;
  private int quantity;
  private Date createdAt;
  private Date updatedAt;
  
}
