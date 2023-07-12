package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegularShipDTO {
  private int regShipNo;
  private String id;
  private String regReceiverName;
  private String regReceiverMobile;
  private String regShipPostcode;
  private String regShipRoadAddress;
  private String regShipJibunAddress;
  private String regShipDetailAddress;
  private String regShipExtraAddress;
  private String regShipMemo;
}
