package com.gdu.pupo.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
	private int noticeNo;
	private int sellerCheck;
	private String id;
  private String noticeCategory;
	private String noticeTitle;
	private String noticeContent;
	private Date noticeCreatedAt;
	private Date noticeModifiedAt;
	private int noticeHit;
	private int attachCount;

}
