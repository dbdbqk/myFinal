package com.gdu.pupo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGradeDTO {
  private String gradeName;
  private double gradeDiscount;
}
