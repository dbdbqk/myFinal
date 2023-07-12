package com.gdu.pupo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.pupo.domain.UserDTO;
import com.gdu.pupo.service.UserService;

import lombok.RequiredArgsConstructor;	

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class UserController {

  // field
  private final UserService userService;
    
  // 이용약관-회원가입
  @GetMapping("/agree.html")
  public String agreeForm() {
    return "user/agree";
  }
  
  // 회원가입
  @GetMapping("/join.html")
  public String joinForm(@RequestParam(value="location", required=false) String location  // 파라미터 location이 전달되지 않으면 빈 문자열("")이 String location에 저장된다.
                       , @RequestParam(value="event", required=false) String event        // 파라미터 event가 전달되지 않으면 빈 문자열("")이 String event에 저장된다.
                       , Model model) {
    model.addAttribute("location", location);
    model.addAttribute("event", event);
    return "user/join";
  }
  
  // 아이디 검사
  @ResponseBody
  @GetMapping(value="/verifyId.do", produces="application/json")
  public Map<String, Object> verifyId(@RequestParam("id") String id) {
    return userService.verifyId(id);
  }
  
  // 이메일 검사
  @ResponseBody
  @GetMapping(value="/verifyEmail.do", produces="application/json")
  public Map<String, Object> verifyEmail(@RequestParam("email") String email) {
    return userService.verifyEmail(email);
  }
  
  // 이메일 인증 코드
  @ResponseBody
  @GetMapping(value="/sendAuthCode.do", produces="application/json")
  public Map<String, Object> sendAuthCode(@RequestParam("email") String email) {
    return userService.sendAuthCode(email);
  }
  
  // 회원가입
  @PostMapping("/join.do")
  public void join(HttpServletRequest request, HttpServletResponse response) {
    userService.join(request, response);
  }
  
  // 로그인
  @GetMapping("/login.html")
  public String loginForm(@RequestHeader("referer") String url, Model model) {
    // 요청 헤더 referer : 로그인 화면으로 이동하기 직전의 주소를 저장하는 헤더 값
    model.addAttribute("url", url);
    return "user/login";
  }
  
  @PostMapping("/login.do")
  public void login(HttpServletRequest request, HttpServletResponse response) {
    userService.login(request, response);
  }
  
  @GetMapping("/logout.do")
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    // 로그인이 되어 있는지 확인
    userService.logout(request, response);
    return "redirect:/";
  }
  
  @GetMapping("/leave.do")
  public void leave(HttpServletRequest request, HttpServletResponse response) {
    // 로그인이 되어 있는지 확인
    userService.leave(request, response);
  }
  
  // 휴면
  @GetMapping("/wakeup.html")
  public String wakeup() {
    return "user/wakeup";
  }
  
  // 휴면 복구
  @GetMapping("/restore.do")
  public void restore(HttpSession session) {
    // 복원할 회원의 아이디를 sysout으로 출력해 보시오.
    System.out.println(session.getAttribute("sleepUserId"));
  }
    
  // 마이페이지
  @GetMapping("/mypage.html")
  public String mypage(HttpSession session, Model model) {  // 마이페이지로 이동
    String id = (String) session.getAttribute("loginId");
    model.addAttribute("loginUser", userService.getUserById(id));
    return "user/mypage";
  }
   
  // 회원정보 수정
  @GetMapping("/modifyInfo.html")
  public String modifyForm(HttpSession session, Model model) {
      // 로그인이 되어 있는지 확인
      if (session.getAttribute("loginId") == null) {
          // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트
          return "redirect:/user/login.html";
      }
      // 세션에서 사용자의 ID를 가져옴
      String id = (String) session.getAttribute("loginId");
      // 서비스를 통해 사용자 정보를 가져옴
      UserDTO user = userService.getUserById(id);
      // 사용자 정보를 모델에 추가
      model.addAttribute("user", user);
      return "user/modifyInfo";
  }

  @PostMapping("/modifyInfo.do")
  public String modify(HttpServletRequest request, HttpServletResponse response) {
      HttpSession session = request.getSession();
      String id = (String) session.getAttribute("loginId");

      UserDTO user = userService.getUserById(id);

      // 조회된 회원 정보에서 필요한 값들을 업데이트
      //user.setPw(request.getParameter("pw"));
      user.setName(request.getParameter("name"));
      user.setGender(request.getParameter("gender"));
      user.setEmail(request.getParameter("email"));
      user.setMobile(request.getParameter("mobile"));
      // 필요한 다른 정보 업데이트

      // 업데이트된 회원 정보를 저장
      userService.modifyUserInfo(user);

      // 업데이트된 회원 정보를 다시 세션에 저장
      session.setAttribute("user", user);

      //return "redirect:/user/modifyInfo.do";
      // 수정이 완료되었음을 알리는 메시지를 세션에 저장
      session.setAttribute("message", "회원 정보가 수정되었습니다.");

      return "redirect:/user/mypage.do";
  }

	  //아이디 찾기
	  @GetMapping("/findId.html")  // 아이디 찾기 화면으로 이동
	  public String findIdForm() {
	    return "user/findId";
	  }
  
  	// 아이디 찾기
	@ResponseBody
	@PostMapping(value="/findId.do", produces="application/json")  // 아이디 찾기
	public Map<String, Object> findId(@RequestParam("name") String name, @RequestParam("email") String email) {
	  return userService.findUser(name, email);
	}
	   
   // 비밀번호 찾기
	@GetMapping("/findPw.html")  // 비밀번호 찾기 화면으로 이동
  public String findPwForm() {
    return "user/findPw";
  }
  
	// 비밀번호 찾기
	@PostMapping("/findPw.do")
	 public void findPw(HttpServletRequest request, HttpServletResponse response) { 
		userService.findPw(request, response);
	}
	  @GetMapping("/sendTemporaryPassword.html")  
	  public String sendTemporaryPasswordForm() {
	    return "user/sendTemporaryPassword";
	  }

	   // 비밀번호  변경
	  @GetMapping("/modifyPw.html")  // 비밀번호  변경 화면으로 이동
	  public String modifyPwForm() {
	    return "user/modifyPw";
	  }
	  
	  // 비밀번호  변경
	  @PostMapping("/modifyPw.do")  // 비밀번호  변경 화면으로 이동
	  public void modifyPw(HttpServletRequest request, HttpServletResponse response) {
		  userService.modifyPw(request, response);
	  }
	  
	  // 사용자-쿠폰 페이지 이동
	  @GetMapping("/coupon.html")
	  public String coupon(HttpServletRequest request, Model model) {
		    return "user/coupon";
	  }
	  
}
	