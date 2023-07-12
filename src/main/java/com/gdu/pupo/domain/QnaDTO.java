package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QnaDTO {
	
	  private int qnaNo;
	  private UserDTO sellerCheck;
    private UserDTO id;
    private String qnaCategory;
    private String qnaTitle;
    private String qnaContent;
    private String qnaEmail;
    private Date qnaCreatedAt;
    private Date qnaModifiedAt;
    private int qnaEmailAlarmCheck;
    private int qnaHit;
    private int qnaStatus;
    private UserDTO userDTO;
    


}
