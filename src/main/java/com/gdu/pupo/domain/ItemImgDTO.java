package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemImgDTO {
  private int imgId;
  private String path;
  private String originName;
  private String filesystemName;
  private int hasThumbnail;
  private int itemId;
}
