package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegularPurchaseDTO {
  private int regPurchaseNo;
  private UserDTO userDTO;
  private String regCustomerUid;
  private RegularShipDTO regularShipDTO;
  private RegularProductDTO regularProductDTO;
  private int regPurchasePrice;
  private int regPurchaseLastPrice;
  private Date regPurchaseAt;
  private String regPg;
  private String regDeliveryStatus;
  private int regProductCount;
  private Date regPaymentAt;
  private String regDeliveryDay;
  private int regPayStatus;
  private Date regLastPayAt;
  private int regPayCount;
  private int regCancel;
}
