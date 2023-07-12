package com.gdu.pupo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.RegularCategoryDTO;
import com.gdu.pupo.domain.RegularProductDTO;

public interface AdminService {
	
	// 회원 조회
//	List<UserDTO> userList();
	
	// 회원 count
	public int userCount();
	public int sellerCount();
	
	// 페이지네이션
	public void getlistUsingPagination(HttpServletRequest request, Model model);
	public void getRegularListPagination(HttpServletRequest request, Model model);
	
	public Map<String, Object> delProduct(int regularNo);
	public RegularProductDTO editProduct(int regularNo);
	public List<RegularCategoryDTO> getRegCategory();
	public int editRegular(MultipartHttpServletRequest multipartRequest);
	
}