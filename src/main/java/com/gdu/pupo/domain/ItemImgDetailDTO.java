package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemImgDetailDTO {
  private int imgDetailId;
  private String pathDetail;
  private String originDetailName;
  private String filesystemDetailName;
  private int hasDetailThumbnail;
  private int itemId;
}
