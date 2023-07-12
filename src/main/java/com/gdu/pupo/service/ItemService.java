package com.gdu.pupo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.ItemDTO;

public interface ItemService {
  
  public List<ItemDTO> getItemsByCategoryId(int categoryId); 
  public List<ItemDTO> getAllItems();                                    // 상품 목록
  public int insertItem(MultipartHttpServletRequest multipartRequest);  // 상품 등록
  public ResponseEntity<byte[]> itemImgDisplay(int itemId);
  public ResponseEntity<byte[]> itemImgDetailDisplay(int itemId);
  
  public void getItem(int itemId, Model model);                      // 상품 상세보기
  public int itemUpdate(MultipartHttpServletRequest multipartRequest);    // 상품 수정
  public int itemDelete(int itemId);                                      // 상품 삭제
  
  
} 