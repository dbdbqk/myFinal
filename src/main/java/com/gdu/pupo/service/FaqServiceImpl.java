package com.gdu.pupo.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gdu.pupo.domain.FaqDTO;
import com.gdu.pupo.mapper.FaqMapper;
import com.gdu.pupo.util.PageUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FaqServiceImpl implements FaqService {
  
  
  private final FaqMapper faqMapper;
  private final PageUtil pageUtil;
  
  @Override
  public void getFaqList(HttpServletRequest request, Model model) {
    
    // 'page' 매개변수가 제공되지 않으면 1로 설정합니다.
    Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
    int page = Integer.parseInt(opt1.orElse("1"));

    // 세션에서 'recordPerPage' 값을 가져옵니다. 세션에 없을 경우 10으로 기본값을 설정합니다.
    HttpSession session = request.getSession();
    Optional<Object> opt2 = Optional.ofNullable(session.getAttribute("recordPerPage"));
    int recordPerPage = (int)(opt2.orElse(10));

    // 'order' 매개변수가 제공되지 않으면 'DESC'로 설정합니다.
    Optional<String> opt3 = Optional.ofNullable(request.getParameter("order"));
    String order = opt3.orElse("DESC");

    // 'column' 매개변수가 제공되지 않으면 'FAQ_NO'로 설정합니다.
    Optional<String> opt4 = Optional.ofNullable(request.getParameter("orderColumn"));
    String orderColumn = opt4.orElse("FAQ_NO");
    
    // 파라미터 searchColumn이 전달되지 않는 경우 column=""로 처리한다. (검색할 칼럼)
    //Optional<String> opt5 = Optional.ofNullable(request.getParameter("searchColumn"));
    //String searchColumn = opt5.orElse("");
    
    // 파라미터 query가 전달되지 않는 경우 query=""로 처리한다. (검색어)
    Optional<String> opt6 = Optional.ofNullable(request.getParameter("query"));
    String query = opt6.orElse("");
    
    // 파라미터 faqCategory가 전달되지 않는 경우 faqCategory=""로 처리한다. (검색어)
    Optional<String> opt7 = Optional.ofNullable(request.getParameter("kind"));
    String kind = opt7.orElse("");


    // 데이터베이스로 전달할 맵(Map)을 생성합니다.
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("query", query);
    map.put("kind", kind);
    int totalRecord = faqMapper.getFaqCount(map);
    
    // column과 query를 이용해 검색된 레코드 개수를 구한다.
  
    int kindCount = faqMapper.getFaqCategoryCount(kind);

    // 'recordPerPage' 값이 변경되었을 때, 현재 페이지의 데이터가 없는 경우를 확인한다.
    int totalPage = (int) Math.ceil((double) totalRecord / recordPerPage);
    if ((page - 1) * recordPerPage >= totalRecord) {
        page = Math.max(totalPage, 1);
    }
    

    // 페이지 유틸리티(PageUtil)를 계산합니다. (페이지네이션에 필요한 모든 정보 포함)
    pageUtil.setPageUtil(page, totalRecord, recordPerPage);

    map.put("begin", pageUtil.getBegin());
    map.put("order", order);
    map.put("recordPerPage", recordPerPage);
    map.put("orderColumn", orderColumn);
    
    
   

    // 지정된 범위(begin ~ end)의 목록을 가져옵니다.
  
    List<FaqDTO> faqList = faqMapper.getFaqList(map);
    
    // pagination.jsp로 전달할 정보를 저장합니다.
    model.addAttribute("faqList",  faqList);
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/customerCenter/faq.html?orderColumn=" + orderColumn + "&order=" + order + "&query=" + query + "&kind=" + kind));
    model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
    model.addAttribute("kindCount", kindCount);
    model.addAttribute("totalRecord", totalRecord);
    model.addAttribute("page", page);
    
  }
  
  @Override
  public void faqAdd(HttpServletRequest request, HttpServletResponse response) {
    try {
      String category = request.getParameter("faqCategory");
      String title = request.getParameter("faqTitle");
      String content = request.getParameter("faqContent");

      FaqDTO faq = new FaqDTO();
      faq.setFaqCategory(category);
      faq.setFaqTitle(title);
      faq.setFaqContent(content);

      int addResult = faqMapper.insertFaq(faq);

      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();

      out.println("<script>");
      if (addResult == 1) {
          out.println("alert('게시글이 등록되었습니다.')");
          out.println("location.href='" + request.getContextPath() + "/customerCenter/faq.html'");
      } else {
          out.println("alert('게시글이 등록되지 않았습니다.')");
          out.println("history.back()");
      }
      out.println("</script>");
      out.flush();
      out.close();
  } catch (Exception e) {
      e.printStackTrace();
  }
    
    
  }
  
  @Override
  public FaqDTO getFaqByNo(HttpServletRequest request) {
    String strFaqNo = request.getParameter("faqNo");
    int faqNo = 0;
   
    
    if(strFaqNo != null && strFaqNo.isEmpty() == false) {
      faqNo = Integer.parseInt(strFaqNo);
    }
      faqMapper.faqHitUp(faqNo);
     return faqMapper.selectFaqByNo(faqNo);
    
  }
  
  @Override
  public void faqModify(HttpServletRequest request, HttpServletResponse response) {
    String title = request.getParameter("faqTitle");
    String category = request.getParameter("faqCategory");
    String content = request.getParameter("faqContent");
    int faqNo =Integer.parseInt(request.getParameter("faqNo"));
    
    
    FaqDTO faqDTO = new FaqDTO();
    faqDTO.setFaqCategory(category);
    faqDTO.setFaqTitle(title);
    faqDTO.setFaqContent(content);
    faqDTO.setFaqNo(faqNo);
    

    
    int modifyResult = faqMapper.updateFaq(faqDTO);
    
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      
      out.println("<script>");
      if(modifyResult == 1) {
        out.println("alert('게시글이 수정되었습니다.')");
        out.println("location.href='" + request.getContextPath() + "/customerCenter/faq.html'");
      } else {
        out.println("alert('게시글이 수정되지 않았습니다.')");
        out.println("history.back()");
      }
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  @Override
  public void faqRemove(HttpServletRequest request, HttpServletResponse response) {

    int faqNo = Integer.parseInt(request.getParameter("faqNo"));

    int removeResult = faqMapper.deleteFaq(faqNo);
    
    try {
      
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      
      out.println("<script>");
      if(removeResult == 1) {
        out.println("alert('게시글이 삭제되었습니다.')");
        out.println("location.href='" + request.getContextPath() + "/customerCenter/faq.html'");
      } else {
        out.println("alert('게시글이 삭제되지 않았습니다.')");
        out.println("history.back()");
      }
      out.println("</script>");
      out.flush();
      out.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
  }
  
  
  

}
