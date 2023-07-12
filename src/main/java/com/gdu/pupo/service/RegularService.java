package com.gdu.pupo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.RegularProductDTO;
import com.gdu.pupo.domain.RegularPurchaseDTO;
import com.gdu.pupo.domain.RegularReviewDTO;

public interface RegularService {

  public void addRegCategory(HttpServletRequest request); // 카테고리 등록
  public void getRegCategory(Model model);   // 카테고리 리스트
  public int addRegular(MultipartHttpServletRequest multipartRequest); // 상품등록
  public void regularList(HttpServletRequest request, Model model); // 상품리스트
  public ResponseEntity<byte[]> regularMainDisplay(int regularNo); // 상품썸네일, 그냥이미지
  public RegularProductDTO regularDetail(int regularNo, Model model); // 상품 상세보기
  public List<RegularReviewDTO> regularReviewList(HttpServletRequest request, Model model, int regularNo); // 리뷰리스트 가져오기
  public ResponseEntity<byte[]> regularDetailDisplay(int regularNo); // 
  public int regularPurchase(HttpServletRequest request, Model model); // 구매완료 후 구매 저장
  public String getToken(); // 아임포트 토큰가져오기
  public String regAgainPay(); // 자동결제
  public RegularPurchaseDTO regularPurchasInfo(int regPurchaseNo, Model model); // 주문정보
  public List<RegularPurchaseDTO> regularMyOrder(HttpServletRequest request, HttpSession session, Model model); // 로그인 아이디 마이 오더 리스트 불러오기
  public Map<String, Object> regCheckReview(HttpServletRequest request); // 리뷰 여부 확인 체크 에이작스 반환
  public int regCancel(int regPurchaseNo); // 구독 취소 예약
  public int regAgain(int regPurchaseNo); // 구독취소예약 상태 재구독 변경
  public Map<String, Object> regDeliverySave(HttpServletRequest request); // 배송정보 변경 저장
  public void regReviewWrite(RegularReviewDTO regularReviewDTO); // 리뷰 작성 저장
  public void getRegModifyReview(int regPurchaseNo, Model model); // 리뷰 수정 페이지 이동 및 기존 작성 내역 가져오기
  public void regModifyReview(RegularReviewDTO regularReviewDTO); // 리뷰 수정 저장
  public Map<String, Object> regDeleteReview(HttpServletRequest request); // 리뷰 삭제
  public Map<String, Object> regAvgStar(HttpServletRequest request); // 리뷰 평점 평균 구하기erList
  public void getRegAdminOrderList(HttpServletRequest request, Model model);  // 관리자 전체 주문 리스트 조회
  
}