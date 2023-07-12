package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VocDTO {
	
	  private int vocNo;
	  private int sellerCheck;
    private String id;
    private int vocCategory;
    private String vocTitle;
    private String vocContent;
    private Date vocCreatedAt;
    private Date vocModifiedAt;
    private int vocHit;
  
}
