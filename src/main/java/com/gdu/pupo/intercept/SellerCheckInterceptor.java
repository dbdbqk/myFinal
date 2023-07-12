package com.gdu.pupo.intercept;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SellerCheckInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    HttpSession session = request.getSession();
    
    // Seller 체크
    if(session != null && !("1".equals(String.valueOf(session.getAttribute("sellerChk"))))) {

      
      // 응답
      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println("<script>");
      out.println("if(confirm('판매자 로그인이 필요한 기능입니다. 로그인하시겠습니까?')){");
      out.println("location.href='" + request.getContextPath() + "/user/login.html';");
      out.println("} else {");
      out.println("history.back();");
      out.println("}");
      out.println("</script>");
      out.flush();
      out.close();
      
      return false;  // 컨트롤러의 요청이 처리되지 않는다.
      
    }
    
    return true;     // 컨트롤러의 요청이 처리된다.
  
  
  }
  
}