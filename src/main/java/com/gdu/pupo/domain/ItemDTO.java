package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
  
  private int itemId;
  private String itemName;
  private int price;
  private String description;
  private int categoryId;
  private int stock;
  
}