package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipDTO {
  private int shipNo;
  private int orderNo;
  private String receiver;
  private String receiverMobile;
  private String shipAddress;
  private String shipNote;
  private String shipPostcode;
  private String shipRoadAddress;
  private String shipJibnuAddress;
  private String shipDetailAddress;
  private String shipExtraAddress;
}
