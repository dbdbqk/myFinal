package com.gdu.pupo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.ItemDTO;
import com.gdu.pupo.domain.ItemImgDTO;
import com.gdu.pupo.domain.ItemImgDetailDTO;

@Mapper
public interface ItemMapper {
  
  public List<ItemDTO> getItemsByCategoryId(int categoryId);        // 카테고리별 리스트
  
  public List<ItemDTO> getAllItems();                               // 상품 리스트
  public List<ItemImgDTO> itemImgList(int itemId);                  // 상품 이미지 리스트
  public List<ItemImgDetailDTO> itemImgDetailList(int itemId);      // 상품 상세 이미지 리스트
  public int insertItem(ItemDTO itemDTO);                           // 상품 등록
  public int insertImg(ItemImgDTO itemImgDTO);                      // 이미지 등록
  public int insertDetailImg(ItemImgDetailDTO itemImgDetailDTO);    // 상세 이미지 등록
  public ItemDTO getItem(int itemId);                               // 상품 상세보기
  
  public ItemImgDTO getImg(int itemId);                             // 이미지 보기
  public ItemImgDetailDTO getDetailImg(int itemId);                 // 상세 이미지 보기
  public int updateItem(ItemDTO itemDTO);                           // 상품 수정
  public int deleteItem(int itemId);                                // 상품 삭제
 
  public void editItemImg(ItemImgDTO itemImgDTO); 
  public void editItemImgDetail(ItemImgDetailDTO itemImgDetailDTO);   // 디테일 이미지  수정


}
