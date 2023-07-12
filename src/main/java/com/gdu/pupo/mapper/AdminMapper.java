package com.gdu.pupo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.RegularCategoryDTO;
import com.gdu.pupo.domain.RegularDetailImgDTO;
import com.gdu.pupo.domain.RegularMainImgDTO;
import com.gdu.pupo.domain.RegularProductDTO;
import com.gdu.pupo.domain.RegularPurchaseDTO;
import com.gdu.pupo.domain.UserDTO;

@Mapper
//public interface AdminMapper {
  public interface AdminMapper extends RegularMapper { // RegularMapper 상속
	
	// 회원 조회
	//public List<UserDTO> selectUserByUserListDTO();
	
	// 회원 count
	//public List<Map<String, Object>> countUsersBySellerCheck();
	public int selectUserCount();
	public int selectSellerCount();
	public List<RegularPurchaseDTO> getlistUsingPagination(Map<String, Object> map);
	public int getListCount();
	public int getListCountRegular();
	public List<RegularProductDTO> getRegularListPagination(Map<String, Object> map);
	
	// 구독상품 삭제
	public int deleteRegularProduct(RegularProductDTO regularProductDTO);
	
	// 구독상품 첨부 삭제(Attach)
	public int removeAttach(int RegularNo);
	public RegularProductDTO selectRegularProduct(int regularNo);
	public int editRegular(RegularProductDTO regularProductDTO);
	public void editRegMainImg(RegularMainImgDTO regularMainImgDTO);
	public void editRegImg(RegularDetailImgDTO regularDetailImgDTO);
	public List<RegularMainImgDTO> getRegularMainImgListInYesterday();
	public List<RegularDetailImgDTO> getRegularDetailImgListInYesterday();
	
	// 카테고리 이름 가져오기
	public RegularCategoryDTO getRegCategory(int RegularCategory);
	
}