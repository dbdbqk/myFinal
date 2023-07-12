package com.gdu.pupo.service;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.GetTokenVO;
import com.gdu.pupo.domain.RegularCategoryDTO;
import com.gdu.pupo.domain.RegularDetailImgDTO;
import com.gdu.pupo.domain.RegularMainImgDTO;
import com.gdu.pupo.domain.RegularProductDTO;
import com.gdu.pupo.domain.RegularPurchaseDTO;
import com.gdu.pupo.domain.RegularReviewDTO;
import com.gdu.pupo.domain.RegularShipDTO;
import com.gdu.pupo.domain.UserDTO;
import com.gdu.pupo.mapper.RegularMapper;
import com.gdu.pupo.util.MyFileUtil;
import com.gdu.pupo.util.PageUtil;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class RegularServicelmpl implements RegularService {
 
  private final RegularMapper regularMapper;
  private final MyFileUtil myFileUtil;
  private final PageUtil pageUtil;
  

  // 카테고리 추가
  @Override
  public void addRegCategory(HttpServletRequest request) { 
    String regularCategoryName = request.getParameter("regularCategoryName");
    RegularCategoryDTO regularCategoryDTO = new RegularCategoryDTO();
    regularCategoryDTO.setRegularCategoryName(regularCategoryName);
    regularMapper.addRegCategory(regularCategoryDTO);
  }
  
  // 카테고리 전체 리스트
  @Override
  public void getRegCategory(Model model) {
    List<RegularCategoryDTO> list = regularMapper.getRegCategoryList();
    model.addAttribute("category", list);
  }
  
  
  // 상품 등록 및 상품 이미지 등록
  @Transactional
  @Override
  public int addRegular(MultipartHttpServletRequest multipartRequest) {
    // regularProductDTO에 저장 할 파라미터 
    String regularName = multipartRequest.getParameter("regularName");
    int regularSellPrice = Integer.parseInt(multipartRequest.getParameter("regularSellPrice"));
    int regularOriginPrice = Integer.parseInt(multipartRequest.getParameter("regularOriginPrice"));
    int regularDisplay = Integer.parseInt(multipartRequest.getParameter("regularDisplay"));
    int regularCategory = Integer.parseInt(multipartRequest.getParameter("regularCategory"));
    int regularState = Integer.parseInt(multipartRequest.getParameter("regularState"));
    String regularSimpleDetail = multipartRequest.getParameter("regularSimpleDetail");
    
    RegularProductDTO regularProductDTO = new RegularProductDTO();
    regularProductDTO.setRegularCategory(regularCategory);
    regularProductDTO.setRegularDisplay(regularDisplay);
    regularProductDTO.setRegularName(regularName);
    regularProductDTO.setRegularOriginPrice(regularOriginPrice);
    regularProductDTO.setRegularSellPrice(regularSellPrice);
    regularProductDTO.setRegularSimpleDetail(regularSimpleDetail);
    regularProductDTO.setRegularState(regularState);
    
    
    int addResult = regularMapper.addRegular(regularProductDTO);
    
    /* regularDetailImg 테이블에 regularDetailImg 넣기 */
    
    // 첨부된 파일 목록
    List<MultipartFile> files = multipartRequest.getFiles("files");  // <input type="file" name="files">
    
    // 첨부된 파일 목록 순회
    for(MultipartFile multipartFile : files) {
      
      // 첨부된 파일이 있는지 체크
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        
        // 예외 처리
        try {
          
          /* HDD에 첨부 파일 저장하기 */
          
          // 첨부 파일의 저장 경로
          String regDetailimgName = myFileUtil.getPath();
          
          // 첨부 파일의 저장 경로가 없으면 만들기
          File dir = new File(regDetailimgName);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          
          // 첨부 파일의 원래 이름
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);  // IE는 전체 경로가 오기 때문에 마지막 역슬래시 뒤에 있는 파일명만 사용한다.
          
          // 첨부 파일의 저장 이름
          String filesystemName = myFileUtil.getFilesystemName(originName);
          
          // 첨부 파일의 File 객체 (HDD에 저장할 첨부 파일)
          File file = new File(dir, filesystemName);
          
          // 첨부 파일을 HDD에 저장
          multipartFile.transferTo(file);  // 실제로 서버에 저장된다.

          // 첨부 파일의 Content-Type 확인
          String contentType = Files.probeContentType(file.toPath());  // 이미지 파일의 Content-Type : image/jpeg, image/png, image/gif, ...
          
          // DB에 저장할 썸네일 유무 정보 처리
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          
          // 첨부 파일의 Content-Type이 이미지로 확인되면 썸네일을 만듬
          if(hasThumbnail) {
            
            // HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file)
              .size(50, 50)
              .toFile(thumbnail);
            
          }
          
          /* DB에 첨부 파일 정보 저장하기 */
          
          // DB로 보낼 DTO 만들기
          RegularDetailImgDTO regularDetailImgDTO = new RegularDetailImgDTO();
          regularDetailImgDTO.setRegDetailImgName(regDetailimgName);
          regularDetailImgDTO.setRegularNo(regularProductDTO.getRegularNo());
          regularDetailImgDTO.setRegFilesystemName(filesystemName);
          
          regularMapper.addRegImg(regularDetailImgDTO);
          
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
  }
    /* regularDetailImg 테이블에 regularDetailImg 넣기 */
    
    // 첨부된 파일 목록
    List<MultipartFile> mainImg = multipartRequest.getFiles("mainImg");  // <input type="file" name="files">
    
    // 첨부된 파일 목록 순회
    for(MultipartFile multipartFile : mainImg) {
      
      // 첨부된 파일이 있는지 체크
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        
        // 예외 처리
        try {
          
          /* HDD에 첨부 파일 저장하기 */
          
          // 첨부 파일의 저장 경로
          String regMainImgName = myFileUtil.getPath();
          
          // 첨부 파일의 저장 경로가 없으면 만들기
          File dir = new File(regMainImgName);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          
          // 첨부 파일의 원래 이름
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);  // IE는 전체 경로가 오기 때문에 마지막 역슬래시 뒤에 있는 파일명만 사용한다.
          
          // 첨부 파일의 저장 이름
          String filesystemName = myFileUtil.getFilesystemName(originName);
          
          // 첨부 파일의 File 객체 (HDD에 저장할 첨부 파일)
          File file = new File(dir, filesystemName);
          
          // 첨부 파일을 HDD에 저장
          multipartFile.transferTo(file);  // 실제로 서버에 저장된다.
          
          // 첨부 파일의 Content-Type 확인
          String contentType = Files.probeContentType(file.toPath());  // 이미지 파일의 Content-Type : image/jpeg, image/png, image/gif, ...
          
          // DB에 저장할 썸네일 유무 정보 처리
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          
          // 첨부 파일의 Content-Type이 이미지로 확인되면 썸네일을 만듬
          if(hasThumbnail) {
            
            // HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file)
              .size(50, 50)
              .toFile(thumbnail);
            
          }
          
          /* DB에 첨부 파일 정보 저장하기 */
          
          // DB로 보낼 DTO 만들기
          RegularMainImgDTO regularMainImgDTO = new RegularMainImgDTO();
          regularMainImgDTO.setRegMainImgName(regMainImgName);
          regularMainImgDTO.setRegFilesystemName(filesystemName);
          regularMainImgDTO.setRegularNo(regularProductDTO.getRegularNo());
          
          // DB로 DTO보내기
          regularMapper.addRegMainImg(regularMainImgDTO);
          
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
  }    
    return addResult;
 }
  
  
  // 전체 상품 리스트
  @Override
  public void regularList(HttpServletRequest request, Model model) {
   
    // 파라미터 regularCategory가 전달되지 않는 경우 regularCategory=""로 처리한다. (카테고리)
    Optional<String> opt1 = Optional.ofNullable(request.getParameter("regularCategory"));
    int regularCategory = Integer.parseInt(opt1.orElse("0"));
    
    // 파라미터 query가 전달되지 않는 경우 query=""로 처리한다. (검색어)
    Optional<String> opt2 = Optional.ofNullable(request.getParameter("query"));
    String query = opt2.orElse("");
    
    Optional<String> opt3 = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt3.orElse("1"));
    
    // 파라미터 regularState가 전달되지 않는 경우 regularState=0로 처리한다.
    Optional<String> opt4 = Optional.ofNullable(request.getParameter("regularState"));
    int regularState = Integer.parseInt(opt4.orElse("0"));
    
    
    // DB로 보낼 Map 만들기(regularCategory + query + regularState)    
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("query", query);
    map.put("regularCategory", regularCategory);
    map.put("regularState", regularState);
    int getRegularCount = regularMapper.getRegularCount();
    
    int recordPerPage = 10;
    
    pageUtil.setPageUtil(page, getRegularCount, recordPerPage);
    
    map.put("begin", pageUtil.getBegin());
    map.put("recordPerPage", recordPerPage);
    
    List<RegularProductDTO> regularList = regularMapper.getRegularList(map);
    List<RegularMainImgDTO> regularMainImgList = regularMapper.getRegularMainImgList();
    model.addAttribute("regularList", regularList);
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/regular/regularList.do?query=" + query + "&regularState=" + regularState + "&regularCategory=" + regularCategory));
    model.addAttribute("regularMainImgList", regularMainImgList);
  }
  
  
  // 메인 이미지 가져오기
  @Override
  public ResponseEntity<byte[]> regularMainDisplay(int regularNo) {
    RegularMainImgDTO regularMainImgDTO = regularMapper.getRegularMainImgByNo(regularNo);
    ResponseEntity<byte[]> image = null;  
    
    try {
      File thumbnail = new File(regularMainImgDTO.getRegMainImgName(), regularMainImgDTO.getRegFilesystemName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
  
  
  // 상품 상세보기 가져오기
  @Override
  public RegularProductDTO regularDetail(int regularNo, Model model) {
    RegularProductDTO regularProductDTO = regularMapper.getRegularByNo(regularNo);
    Double regAvgStar1 = regularMapper.regAvgStar(regularNo);
    double regAvgStar = 0;

    if (regAvgStar1 != null) {
      DecimalFormat decimalFormat = new DecimalFormat("#.##");
      regAvgStar = Double.parseDouble(decimalFormat.format(regAvgStar1));
    }
    model.addAttribute("regAvgStar", regAvgStar);
    return regularProductDTO;
  }
  
  // 상품 상세보기 이미지 가져오기
  @Override
  public ResponseEntity<byte[]> regularDetailDisplay(int regularNo) {
    RegularDetailImgDTO regularDetailImgDTO = regularMapper.getRegularImgByNo(regularNo);
    ResponseEntity<byte[]> image = null;  
    
    try {
      File thumbnail = new File(regularDetailImgDTO.getRegDetailImgName(), regularDetailImgDTO.getRegFilesystemName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
  
  
  // 구매 완료 후 구매 정보 저장
  @Transactional
  @Override
  public int regularPurchase(HttpServletRequest request, Model model) {
    // 결제정보, 아이디 가져오기
    String regCustomerUid = request.getParameter("regCustomerUid");
    String id = request.getParameter("loginId");
    int regularNo = Integer.parseInt(request.getParameter("regularNo"));
    int regPurchasePrice = Integer.parseInt(request.getParameter("regPurchasePrice"));
   
    RegularShipDTO regularShipDTO = new RegularShipDTO();
    
    // 배송정보 저장
    String regReceiverName = request.getParameter("name");
    String regReceiverMobile = request.getParameter("mobile");
    String regShipPostcode = request.getParameter("postcode");
    String regShipRoadAddress = request.getParameter("roadAddress");
    String regShipJibunAddress = request.getParameter("jibunAddress");
    String regShipDetailAddress = request.getParameter("detailAddress");
    String regShipExtraAddress = request.getParameter("extraAddress");
    
    regularShipDTO.setId(id);
    regularShipDTO.setRegReceiverMobile(regReceiverMobile);
    regularShipDTO.setRegReceiverName(regReceiverName);
    regularShipDTO.setRegShipDetailAddress(regShipDetailAddress);
    regularShipDTO.setRegShipExtraAddress(regShipExtraAddress);
    regularShipDTO.setRegShipJibunAddress(regShipJibunAddress);
    regularShipDTO.setRegShipMemo("테스트");
    regularShipDTO.setRegShipPostcode(regShipPostcode);
    regularShipDTO.setRegShipRoadAddress(regShipRoadAddress);
    
    
    regularMapper.addRegShip(regularShipDTO);
    
    // 이부분은 주문 완료 될 때 배송 정보들 입력 되면서 배송번호 불러와야함
    int regShipNo = regularShipDTO.getRegShipNo(); 
    // 실제 주문 페이지에서 보내주기
    int regProductCount = Integer.parseInt(request.getParameter("regCount")); 
    int totalPrice = regPurchasePrice * regProductCount;
    
    // 고정으로 5% 할인 들어감 및 소수점은 절삭
    double discountedPrice = totalPrice * 0.95;
    int regPurchaseLastPrice = (int) Math.floor(discountedPrice);
    
    // 신용카드,카카오페이 결제 선택
    String regPg = request.getParameter("regPg");
    
    // 실제 주문 페이지에서 월화수 수목금 선택
    String regDeliverDay = request.getParameter("regDeliveryDay");
    String regDeliveryStatus = "배송중";
    
    
    RegularPurchaseDTO regularPurchaseDTO = new RegularPurchaseDTO();
    UserDTO userDTO = new UserDTO();
    RegularProductDTO regularProductDTO = new RegularProductDTO();
    regularProductDTO.setRegularNo(regularNo);
    userDTO.setId(id);
    regularShipDTO.setRegShipNo(regShipNo);
    regularPurchaseDTO.setUserDTO(userDTO);
    regularPurchaseDTO.setRegCustomerUid(regCustomerUid);
    regularPurchaseDTO.setRegDeliveryDay(regDeliverDay);
    regularPurchaseDTO.setRegDeliveryStatus(regDeliveryStatus);
    regularPurchaseDTO.setRegProductCount(regProductCount);
    regularPurchaseDTO.setRegPurchasePrice(regPurchasePrice);
    regularPurchaseDTO.setRegPg(regPg);
    regularPurchaseDTO.setRegularShipDTO(regularShipDTO);
    regularPurchaseDTO.setRegularProductDTO(regularProductDTO);
    regularPurchaseDTO.setRegPurchaseLastPrice(regPurchaseLastPrice);
    regularMapper.addRegPurchase(regularPurchaseDTO);

    int regPurchaseNo = regularPurchaseDTO.getRegPurchaseNo();
    return regPurchaseNo;
  }
  
  // 구매완료 후 구매완료 페이지 이동
  @Override
  public RegularPurchaseDTO regularPurchasInfo(int regPurchaseNo, Model model) {
    RegularPurchaseDTO regularPurchaseDTO = new RegularPurchaseDTO();
    regularPurchaseDTO = regularMapper.getRegularPayDone(regPurchaseNo);
    String id = regularPurchaseDTO.getUserDTO().getId();
    model.addAttribute("id", id);
    return regularPurchaseDTO;
  }
  

  // api키 가져오기
  @Value("${import.api.restApiKey}")
  private String restApiKey;
  @Value("${import.api.restApiSecret}")
  private String restApiSecret;
  
  
  // 아임포트 API 토큰 가져오기
  @Override
  public String getToken() { // 아임포트 토큰 받아오기
    RestTemplate restTemplate = new RestTemplate();
    
    //서버로 요청할 Header
     HttpHeaders headers = new HttpHeaders();
     headers.setContentType(MediaType.APPLICATION_JSON);
    
     Map<String, Object> map = new HashMap<>();
     map.put("imp_key", restApiKey);
     map.put("imp_secret", restApiSecret);
      
     Gson var = new Gson();
     String json=var.toJson(map);
    //서버로 요청할 Body
     
    HttpEntity<String> entity = new HttpEntity<>(json,headers);
    return restTemplate.postForObject("https://api.iamport.kr/users/getToken", entity, String.class);
    }
  
  
  
  // 최종결제시점 기준 1개월 후 자동 결제 및 구독취소예약 구독 종료로 만들기
  @Override
    public String regAgainPay() {
      String token = getToken();
      Gson str = new Gson();
      token = token.substring(token.indexOf("response") + 10);
      token = token.substring(0, token.length() - 1);
  
      GetTokenVO vo = str.fromJson(token, GetTokenVO.class);
  
      String access_token = vo.getAccess_token();
      System.out.println(access_token);
  
      RestTemplate restTemplate = new RestTemplate();
  
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(access_token);
      
      
      // 현재 날짜 가져오기 구독취소요청용
      LocalDate currentDate1 = LocalDate.now();
      
      // 구독취소 요청 주문들 출력
      List<RegularPurchaseDTO> cancelList = regularMapper.regularCancelList();
      
      // 구독취소 요청 한 주문들 마지막 결제일 기준 1달뒤 취소로 변경
      for(RegularPurchaseDTO cancel : cancelList) {
        // Date.util -> LocalDate로 변경
        Date regLastPayAt = cancel.getRegLastPayAt();
        Instant instant = regLastPayAt.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDate regLastPayDate = localDateTime.toLocalDate();
        // 다음 결제일 계산 1달 뒤임
        LocalDate regNextPayDate =  regLastPayDate.plusMonths(1); 
        if(currentDate1.equals(regNextPayDate) || currentDate1.isAfter(regNextPayDate)) {
          regularMapper.updateRegCancelDone(cancel.getRegPurchaseNo());
        }
      }
      
      
      // 정기구독 인 주문들만 출력
      List<RegularPurchaseDTO> purchaseList = regularMapper.regularPayList();
      
      // 현재 날짜 얻어오기
      LocalDate currentDate = LocalDate.now();
      LocalDateTime merchant = LocalDateTime.now();
      
      
      
      // 결제 주기
      for(RegularPurchaseDTO purchase : purchaseList) {
        // Date.util -> LocalDate로 변경
        Date regLastPayAt = purchase.getRegLastPayAt();
        Instant instant = regLastPayAt.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDate regLastPayDate = localDateTime.toLocalDate();
        // 다음 결제일 계산 1달 뒤임
        LocalDate regNextPayDate =  regLastPayDate.plusMonths(1);
        if(currentDate.equals(regNextPayDate) || currentDate.isAfter(regNextPayDate)){
          Map<String, Object> map = new HashMap<>();
          map.put("customer_uid", purchase.getRegCustomerUid());
          map.put("merchant_uid", "pupo_" + merchant);
          map.put("amount", purchase.getRegPurchaseLastPrice());
          map.put("name", "풀파워 샐러드 정기결제");
          
          Gson var = new Gson();
          String json = var.toJson(map);
         
          HttpEntity<String> entity = new HttpEntity<>(json, headers);
          
          restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/again", entity, String.class);
          regularMapper.regularPayUpdate(purchase.getRegPurchaseNo());
        }
      }
      return null;
  }
  
  // 로그인 아이디 마이 오더 리스트 불러오기
  @Override
  public List<RegularPurchaseDTO> regularMyOrder(HttpServletRequest request, HttpSession session, Model model) {
    
    // 파라미터 regPayStatus가 전달되지 않는 경우 regPayStatus=""로 처리한다.
    Optional<String> opt1 = Optional.ofNullable(request.getParameter("regPayStatus"));
    String regPayStatus = opt1.orElse("");
    
    Optional<String> opt2 = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt2.orElse("1"));
    
    // 구독 취소 예약
    Optional<String> opt3 = Optional.ofNullable(request.getParameter("cancel"));
    String cancel = opt3.orElse("");
    // DB로 보낼 Map 만들기( query, cancel)    
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("regPayStatus", regPayStatus);
    map.put("cancel", cancel);
    int recordPerPage = 5;
    String id = (String) session.getAttribute("loginId");
    int getMyOrderCount = regularMapper.getRegularMyOrderCount(id);
    pageUtil.setPageUtil(page, getMyOrderCount, recordPerPage);
    
    map.put("begin", pageUtil.getBegin());
    map.put("recordPerPage", recordPerPage);
    
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/regular/regularMyOrder.html?" + "regPayStatus=" + regPayStatus));
    
    map.put("id", id);
    List<RegularPurchaseDTO> regMyOrderList = regularMapper.getRegularMyOrder(map);
    
    // 한달 뒤 결제일 미리 계산해서 html에 보내주기
    List<Date> oneMonthList = new ArrayList<>();
    
    Calendar cal = Calendar.getInstance();
   
    for (RegularPurchaseDTO regPurchase : regMyOrderList) {
      cal.setTime(regPurchase.getRegLastPayAt());
      cal.add(Calendar.MONTH, 1);
      Date oneMonthLater = cal.getTime();
      oneMonthList.add(oneMonthLater);
    }
    model.addAttribute("oneMonth", oneMonthList);
    return regMyOrderList;
  }
  
  // 리뷰 리스트 가져오기 -> 마이페이지에서 리뷰 작성 여부 확인해서 리뷰작성, 리뷰수정, 삭제 버튼 나오게 할 예정
  @Override
  public Map<String, Object> regCheckReview(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    int regPurchaseNo = Integer.parseInt(request.getParameter("regPurchaseNo"));
    int check = regularMapper.regCheckReview(regPurchaseNo);
    map.put("check", check);
    return map;
  }
  
  
  // 구독 취소 예약
  @Override
  public int regCancel(int regPurchaseNo) {
    int updateResult = regularMapper.updateRegCancel(regPurchaseNo);
    System.out.println("테스트" + updateResult);
    return updateResult;
  }
  
  // 구독 취소 예약 상태 재구독으로 변경
  @Override
  public int regAgain(int regPurchaseNo) {
    int updateResult = regularMapper.updateRegAgain(regPurchaseNo);
    return updateResult;
  }
  
  // 배송정보 저장 에이작스로 반환
  @Override
  public Map<String, Object> regDeliverySave(HttpServletRequest request) {
    RegularShipDTO regularShipDTO = new RegularShipDTO();
    // 변경할 배송정보 넘버
    int regShipNo = Integer.parseInt(request.getParameter("regShipNo"));
    // 배송정보 저장
    String regReceiverName = request.getParameter("name");
    String regReceiverMobile = request.getParameter("mobile");
    String regShipPostcode = request.getParameter("postcode");
    String regShipRoadAddress = request.getParameter("roadAddress");
    String regShipJibunAddress = request.getParameter("jibunAddress");
    String regShipDetailAddress = request.getParameter("detailAddress");
    String regShipExtraAddress = request.getParameter("extraAddress");
    
    // 배송정보 DTO에 저장
    regularShipDTO.setRegShipNo(regShipNo);
    regularShipDTO.setRegReceiverMobile(regReceiverMobile);
    regularShipDTO.setRegReceiverName(regReceiverName);
    regularShipDTO.setRegShipDetailAddress(regShipDetailAddress);
    regularShipDTO.setRegShipExtraAddress(regShipExtraAddress);
    regularShipDTO.setRegShipJibunAddress(regShipJibunAddress);
    regularShipDTO.setRegShipPostcode(regShipPostcode);
    regularShipDTO.setRegShipRoadAddress(regShipRoadAddress);
    
    int saveResult = regularMapper.regSaveDelivery(regularShipDTO);
    Map<String, Object> map = new HashMap<>();
    map.put("saveResult", saveResult);
    return map;
  }
  
  // 리뷰 작성
  @Override
  public void regReviewWrite(RegularReviewDTO regularReviewDTO) {
    regularMapper.regWriteReview(regularReviewDTO);
  }
  
  // 작성 리뷰내역 가져오기
  @Override
  public void getRegModifyReview(int regPurchaseNo, Model model) {
    RegularReviewDTO regularReviewDTO = new RegularReviewDTO();
    regularReviewDTO = regularMapper.getRegModifyReview(regPurchaseNo);
    model.addAttribute("review", regularReviewDTO);
  }
  
  // 리뷰 수정 저장
  @Override
  public void regModifyReview(RegularReviewDTO regularReviewDTO) {
    System.out.println(regularReviewDTO.getRegReviewNo() + "이요");
    regularMapper.updateRegModifyReview(regularReviewDTO);
  }
  
  // 리뷰 삭제
  @Override
  public Map<String, Object> regDeleteReview(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    int regPurchaseNo = Integer.parseInt(request.getParameter("regPurchaseNo"));
    int result = regularMapper.regDeleteReview(regPurchaseNo);
    map.put("deleteResult", result);
    return map;
  }
  
  // 리뷰 리스트 가져오기
  @Override
  public List<RegularReviewDTO> regularReviewList(HttpServletRequest request, Model model, int regularNo) {
    Optional<String> opt3 = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt3.orElse("1"));
    // DB로 보낼 Map 만들기(column + query)    
    Map<String, Object> map = new HashMap<String, Object>();
    
    int regReviewCount = regularMapper.regReviewCount(regularNo);
    
    int recordPerPage = 5;
    
    pageUtil.setPageUtil(page, regReviewCount, recordPerPage);
    map.put("regularNo", regularNo);
    map.put("begin", pageUtil.getBegin());
    map.put("recordPerPage", recordPerPage);
    
    
    List<RegularReviewDTO> regReviewList = regularMapper.getRegularReviewList(map);
    model.addAttribute("regReviewList", regReviewList);
    model.addAttribute("regularNo",regularNo);
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/regular/regularDetail.do?regularNo=" + regularNo));
    
    return null;
  }
  
  // 리뷰 평균 ajax
  @Override
  public Map<String, Object> regAvgStar(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    int regularNo = Integer.parseInt(request.getParameter("regularNo"));
    Double regAvgStar1 = regularMapper.regAvgStar(regularNo);
    double regAvgStar = 0.0;

    if (regAvgStar1 != null) {
      DecimalFormat decimalFormat = new DecimalFormat("#.##");
      regAvgStar = Double.parseDouble(decimalFormat.format(regAvgStar1));
    }

    map.put("regAvgStar", regAvgStar);
    return map;
  }
  
  @Override
  public void getRegAdminOrderList(HttpServletRequest request, Model model) {
    
    
    Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt1.orElse("1"));
    
    // DB로 보낼 Map 만들기( query, cancel)    
    Map<String, Object> map = new HashMap<String, Object>();
    int recordPerPage = 10;
    int regOrderListCount = regularMapper.regOrderListCount(); // 전체 구매 가져오기
    pageUtil.setPageUtil(page, regOrderListCount, recordPerPage);
    
    map.put("begin", pageUtil.getBegin());
    map.put("recordPerPage", recordPerPage);
    
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/regular/adminRegOrder.do"));
    
    List<RegularPurchaseDTO> regOrderList = regularMapper.getRegOrderList(map);
    
    // 한달 뒤 결제일 미리 계산해서 html에 보내주기
    List<Date> oneMonthList = new ArrayList<>();
    
    Calendar cal = Calendar.getInstance();
   
    for (RegularPurchaseDTO regPurchase : regOrderList) {
      cal.setTime(regPurchase.getRegLastPayAt());
      cal.add(Calendar.MONTH, 1);
      Date oneMonthLater = cal.getTime();
      oneMonthList.add(oneMonthLater);
    }
    model.addAttribute("oneMonth", oneMonthList);
    model.addAttribute("regOrderList", regOrderList);
  }
 
  
}