package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
  private int reviewNo;
  private int orderNo;
  private String reviewTitle;
  private String reviewContent;
  private String reviewImg;
  private int reviewScore;
  private Date regAt;
  private Date updateAt;
}
