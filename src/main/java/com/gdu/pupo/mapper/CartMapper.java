package com.gdu.pupo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.CartItemDTO;

@Mapper
public interface CartMapper {

  public int addCart(CartItemDTO cartItemDTO);  // 카트 등록
  public List<CartItemDTO> selectCartList(String id);
  public int deleteCart(List<String> cartItemIdList); // 카트 삭제
  
  
  
  
  
  
  
  
  
  
  /*
   * public CartDTO checkCart(CartDTO cartDTO); // 카트 확인 public int
   * addCart(CartDTO cartDTO); // 카트 추가 public List<CartDTO> getCartById(String
   * id); // 카트 목록
   * 
   * public int deleteCart(int cartId); // 카트 삭제 public int modifyCart(CartDTO
   * cartDTO); // 카트 수정
   */
}