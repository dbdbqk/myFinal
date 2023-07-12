package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
  private int couponNo;
  private String couponName;
  private double couponSale;
  private int couponUse;
  private String couponBeginAt;
  private String couponEndAt;
}