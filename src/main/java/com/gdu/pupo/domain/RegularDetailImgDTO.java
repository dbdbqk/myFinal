package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegularDetailImgDTO {
  private int regDetailImgNo;
  private int regularNo;
  private String regDetailImgName;
  private String regFilesystemName;
}
