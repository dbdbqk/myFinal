package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private int orderNo;
  private int cartNo;
  private int totalPay;
  private int totalSum;
  private int totalDiscount;
  private int shipCharge;
  private Date orderAt;
  private String orderState;
}
