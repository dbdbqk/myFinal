package com.gdu.pupo.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface QnaService {
  
  public void getQnaList(HttpServletRequest request, Model model);
  public void getQnaListAdmin(HttpServletRequest request, Model model);
  public void qnaAdd(MultipartHttpServletRequest multipartRequest, HttpServletResponse response);
  public void getQnaByNo(int qnaNo, Model model);
  public ResponseEntity<byte[]> qnaDisplay(int attachNo);
  public ResponseEntity<Resource> qnaDownload(int attachNo, String userAgent);

}
