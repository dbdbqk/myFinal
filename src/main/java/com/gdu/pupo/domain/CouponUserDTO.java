package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponUserDTO {
  private int couponNo;
  private String id;
  private Date couponCreatedAt;
  private int couponUsed;
}