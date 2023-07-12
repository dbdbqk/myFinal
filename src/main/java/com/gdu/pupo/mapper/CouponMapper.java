package com.gdu.pupo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.gdu.pupo.domain.CouponDTO;
import com.gdu.pupo.domain.CouponUserDTO;

@Mapper
public interface CouponMapper {
	
// 관리자
	public int insertAddCoupon(CouponDTO couponDTO); // 쿠폰 생성
	public List<CouponDTO> selectUserCouponList(); // 쿠폰조회
	//public CouponDTO getCoupon(int couponNo); 
	//public void updateCoupon(CouponDTO coupon);
	public String selectgetEventCoupon(CouponUserDTO couponUserDTO);
  
// 사용자
	public void insertCouponUser(CouponUserDTO couponUser);
	public CouponUserDTO getCouponUser(int couponNo, String id);
	public void insertGetEventCoupon(CouponUserDTO couponUser);
	//public void updateCouponUser(CouponUserDTO couponUser);
	public int deleteCoupon(CouponDTO couponDTO);
} 