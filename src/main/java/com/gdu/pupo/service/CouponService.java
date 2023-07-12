package com.gdu.pupo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gdu.pupo.domain.CouponDTO;

public interface CouponService {

	// 쿠폰 등록
	public int addCoupon(HttpServletRequest request, HttpServletResponse response);
	
	// 쿠폰 조회
	public List<CouponDTO> couponList();
	
	// 이벤트 페이지 - 쿠폰 발급
	public void getEventCoupon(HttpServletRequest request, HttpServletResponse response);

	// 쿠폰 삭제
	public Map<String, Object> delCoupon(int couponNo);
	
	

}