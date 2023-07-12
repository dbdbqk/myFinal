package com.gdu.pupo.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface NoticeService {
  
  public void getNoticeList(HttpServletRequest request, Model model);
  public void noticeAdd(MultipartHttpServletRequest multipartRequest, HttpServletResponse response);
  public void getNoticeByNo(int noticeNo, Model model);
  public int noticeModify(MultipartHttpServletRequest multipartRequest);
  public int noticeRemove(int noticeNo);
  
 
  public ResponseEntity<byte[]> noticeDisplay(int attachNo);
  public ResponseEntity<Resource> noticeDownload(int attachNo, String userAgent);
  public ResponseEntity<Resource> noticeDownloadAll(int noticeNo);
  public int noticeAttachRemove(int attachNo);

  

}
