package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqDTO {
	
	  private int faqNo;
	  private int sellerCheck;
	  private String id;
	  private String faqCategory;
	  private String faqTitle;
	  private String faqContent;
	  private Date faqCreatedAt;
	  private Date faqModifiedAt;
	  private int faqHit;
	  

	 
}
