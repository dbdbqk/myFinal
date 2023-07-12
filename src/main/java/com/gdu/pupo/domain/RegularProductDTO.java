package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegularProductDTO {
  private int regularNo;
  private String regularName;
  private int regularSellPrice;
  private int regularOriginPrice;
  private int regularDisplay;
  private int regularStock;
  private int regularCategory;
  private int regularState;
  private String regularSimpleDetail;
}
