package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegularMainImgDTO {
  private int regMainImgNo;
  private int regularNo;
  private String regMainImgName;
  private String regFilesystemName;
}
