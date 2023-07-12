package com.gdu.pupo.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.gdu.pupo.domain.CouponDTO;
import com.gdu.pupo.domain.CouponUserDTO;
import com.gdu.pupo.mapper.CouponMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CouponServiceImpl implements CouponService {
	
	// field
	private final CouponMapper couponMapper;
	
	@Override
	public int addCoupon(HttpServletRequest request, HttpServletResponse response) {
		

		String couponName = request.getParameter("couponName");
		String couponSale = request.getParameter("couponSale");
		//String couponUse = request.getParameter("couponUse");
		String couponBeginAt = request.getParameter("couponBeginAt");
		String couponEndAt = request.getParameter("couponEndAt");
		//return couponMapper.couponList();
		
		CouponDTO couponDTO = new CouponDTO();
		couponDTO.setCouponName(couponName);
		couponDTO.setCouponSale(Double.parseDouble(couponSale));
		//couponDTO.setCouponUse(Integer.parseInt(couponUse));
		couponDTO.setCouponBeginAt(couponBeginAt);
		couponDTO.setCouponEndAt(couponEndAt);
		
		int addCouponResult = couponMapper.insertAddCoupon(couponDTO);

		System.out.println(addCouponResult);
		try {

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			if (addCouponResult == 1) {
				out.println("alert('쿠폰 등록이 완료되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/admin/couponList.html';");
			} else {
				out.println("alert('쿠폰 등록이 실패했습니다.');");
				out.println("history.go(-2);");
			}
			out.println("</script>");
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return addCouponResult;

	}
	
	@Override
	public List<CouponDTO> couponList() {
		List<CouponDTO> selectUserCouponDTO = couponMapper.selectUserCouponList();		
		return selectUserCouponDTO;
	}
	
	@Override
	public void getEventCoupon(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		String loginId = (String) session.getAttribute("loginId");
		CouponUserDTO couponUserDTO = new CouponUserDTO();
		couponUserDTO.setId(loginId);
				
		// 웰컴쿠폰 쿠폰넘버 1
		String couponNo = couponMapper.selectgetEventCoupon(couponUserDTO);
		
		try {
		
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<script>");
		// 0 쿠폰없음, 발행가능
		if("0".equals(couponNo)) {

			couponMapper.insertGetEventCoupon(couponUserDTO);
			
			out.println("alert('쿠폰 발행되었습니다.');");
			out.println("location.href='" + request.getContextPath() + "/admin/eventCoupon.html';");

			// 1 쿠폰있음, 발행불가
		} else {
			out.println("alert('이미 발행된 쿠폰입니다.');");
		    out.println("history.back();");
		}
		out.println("</script>");
		out.flush();
		out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// 쿠폰 삭제
	@Override
	public Map<String, Object> delCoupon(int couponNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		CouponDTO couponDTO = new CouponDTO();
		couponDTO.setCouponNo(couponNo);
		map.put("delYn", couponMapper.deleteCoupon(couponDTO));
		
		return map;
	}
}