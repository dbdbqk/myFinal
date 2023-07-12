package com.gdu.pupo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.pupo.service.FaqService;
import com.gdu.pupo.service.NoticeService;
import com.gdu.pupo.service.QnaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customerCenter")
public class CustomerCenterController {
  
  private final FaqService faqService;
  private final NoticeService noticeService;
  private final QnaService qnaService;
  
  
  /* 고객센터 홈 */
  @GetMapping("/centerHome.html")
  public String centerHome(HttpServletRequest request, Model model) {
    //session에 올라간 recordPerPage 값 날려주기
    if(request.getHeader("referer").contains("centerHome.html") == false) {
      request.getSession().removeAttribute("recordPerPage");
    }
    noticeService.getNoticeList(request, model);
    faqService.getFaqList(request, model);
    return "customerCenter/centerHome";  
  }

  
  
  
  @GetMapping("/qna.html")
  public String qna(Model model) {
      return "customerCenter/qna";
  }
 
  /* 1:1문의글쓴거 더하기 */
  @PostMapping("/qnaAdd.do")
  public void qnaAdd(MultipartHttpServletRequest multipartRequest,  HttpServletResponse response) {
    qnaService.qnaAdd(multipartRequest, response);

  }
  

  /* 1:1문의 */
  @GetMapping("/qnaList.html")
  public String qnaList(HttpServletRequest request, Model model) {
    //session에 올라간 recordPerPage 값 날려주기
    if(request.getHeader("referer").contains("qnaList.html") == false) {
      request.getSession().removeAttribute("recordPerPage");
    }
    qnaService.getQnaList(request, model);
    return "customerCenter/qnaList";  
  }
  
  /* 1:1문의 - 관리자 */
  @GetMapping("/qnaListAdmin.html")
  public String qnaListAdmin(HttpServletRequest request, Model model) {
    //session에 올라간 recordPerPage 값 날려주기
    if(request.getHeader("referer").contains("qnaListAdmin.html") == false) {
      request.getSession().removeAttribute("recordPerPage");
    }
    qnaService.getQnaListAdmin(request, model);
    return "customerCenter/qnaListAdmin";  
  }
  
  
  // qna디테일 -관리자 화면가기
  @GetMapping("/qnaDetailAdmin.html")
  public String qnaDetail(@RequestParam(value="qnaNo", required=false, defaultValue="0") int qnaNo
                     , Model model) {
    qnaService.getQnaByNo(qnaNo, model);
    return "customerCenter/qnaDetailAdmin";
  }
  

  // qna 게시글에 첨부된 이미지 화면에 보여주기
  @GetMapping("/qnaDisplay.do")
  public ResponseEntity<byte[]> qnaDisplay(@RequestParam("attachNo") int attachNo) {
    return qnaService.qnaDisplay(attachNo);
  }

// qna 게시글 첨부파일 다운받기
  @GetMapping("/qnaDownload.do")
  public ResponseEntity<Resource> qnaDownload(@RequestParam("attachNo") int attachNo, @RequestHeader("User-Agent") String userAgent) {
    return qnaService.qnaDownload(attachNo, userAgent);
  }

  
  /*
  @GetMapping("/list.do")
  public String list(HttpServletRequest request, Model model) {
    blogService.loadBlogList(request, model);
    return "blog/list";
  }
  
  @GetMapping("/write.form")
  public String write() {
    return "blog/write";
  }
  
 
  @ResponseBody
  @PostMapping(value="/imageUpload.do", produces="application/json")
  public Map<String, Object> imageUpload(MultipartHttpServletRequest multipartRequest) {
    return blogService.imageUpload(multipartRequest);
  }
  
  @GetMapping("/increseHit.do")
  public String increseHit(@RequestParam(value="blogNo", required=false, defaultValue="0") int blogNo) {
    int increaseResult = blogService.increaseHit(blogNo);
    if(increaseResult == 1) {
      return "redirect:/blog/detail.do?blogNo=" + blogNo;
    } else {
      return "redirect:/blog/list.do";
    }
  }
  
  @GetMapping("/detail.do")
  public String detail(@RequestParam(value="blogNo", required=false, defaultValue="0") int blogNo
                     , Model model) {
    blogService.loadBlog(blogNo, model);
    return "blog/detail";
  }
  
  
  */
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /* 고객의 소리 */
  @GetMapping("/voc.html")
  public String voc() {
    return "customerCenter/voc";  
  }
  
  
  /* notice */
  @GetMapping("/notice.html")
  public String notice(HttpServletRequest request, Model model) {
    //session에 올라간 recordPerPage 값 날려주기
    if(request.getHeader("referer").contains("notice.html") == false) {
      request.getSession().removeAttribute("recordPerPage");
    }
    noticeService.getNoticeList(request, model);
    return "customerCenter/notice";  
  }
  
  /* notice 작성화면 */
  @GetMapping("/noticeWrite.html")
  public String noticeWritel() {
    return "customerCenter/noticeWrite";  
  }
  
  /* notice 글쓴거 더하기 */
  @PostMapping("/noticeAdd.do")
  public void noticeAdd(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
    noticeService.noticeAdd(multipartRequest, response);
  }


  // notice디테일 화면가기
  @GetMapping("/noticeDetail.html")
  public String noticeDetail(@RequestParam(value="noticeNo", required=false, defaultValue="0") int noticeNo
                     , Model model) {
    noticeService.getNoticeByNo(noticeNo, model);
    return "customerCenter/noticeDetail";
  }
  // notice 게시글에 첨부된 이미지 화면에 보여주기
  @GetMapping("/noticeDisplay.do")
  public ResponseEntity<byte[]> noticeDisplay(@RequestParam("attachNo") int attachNo) {
    return noticeService.noticeDisplay(attachNo);
  }

// notice 게시글 첨부파일 다운받기
  @GetMapping("/noticeDownload.do")
  public ResponseEntity<Resource> noticeDownload(@RequestParam("attachNo") int attachNo, @RequestHeader("User-Agent") String userAgent) {
    return noticeService.noticeDownload(attachNo, userAgent);
  }
  
 //notice 게시글 수정화면
  @PostMapping("/noticeEdit.html")
  public String noticeEdit(@RequestParam(value="noticeNo", required=false, defaultValue="0") int noticeNo
      , Model model) {
    noticeService.getNoticeByNo(noticeNo, model);
    return "customerCenter/noticeEdit";
  }

 // notice 게시글 수정하기
  @PostMapping("/noticeModify.do")
  public String noticeModify(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) {
    int modifyResult = noticeService.noticeModify(multipartRequest);
    redirectAttributes.addFlashAttribute("modifyResult", modifyResult);
    return "redirect:/customerCenter/noticeDetail.html?noticeNo=" + multipartRequest.getParameter("noticeNo");
  }

// notice 게시글 삭제하기
  @PostMapping("/noticeRemove.do")
  public String noticeRemove(@RequestParam("noticeNo") int noticeNo, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("removeResult", noticeService.noticeRemove(noticeNo));
    return "redirect:/customerCenter/notice.html";
  }

  
  @GetMapping("/noticeAttachRemove.do")
  public String noticeAttachRemove(@RequestParam("noticeNo") int noticeNo, @RequestParam("attachNo") int attachNo) {
    noticeService.noticeAttachRemove(attachNo);
    return "redirect:/customerCenter/noticeDetail.html?noticeNo=" + noticeNo;
  }
  
  
  
  
  
  
  
  
  
  /* faq */
  @GetMapping("/faq.html") 
  public String faq(HttpServletRequest request, Model model) {
    //session에 올라간 recordPerPage 값 날려주기
    if(request.getHeader("referer").contains("faq.html") == false) {
      request.getSession().removeAttribute("recordPerPage");
    }
    faqService.getFaqList(request, model);
    return "customerCenter/faq"; 
  }
  /* faq 글쓰기(관리자 권한이라서 PostMapping) */
  @PostMapping("/faqWrite.html")
  public String write() {
    return "customerCenter/faqWrite";
  }
  /* faq글쓴거 수정화면 */
  @PostMapping("/faqEdit.html")
  public String faqEdit(HttpServletRequest request, Model model) {
    model.addAttribute("f", faqService.getFaqByNo(request));
    return "customerCenter/faqEdit";
  }
  /* faq 글쓴거 더하기 */
  @PostMapping("/faqAdd.do")
  public void faqAdd(HttpServletRequest request, HttpServletResponse response) {
    faqService.faqAdd(request, response);
  }
  /* faq 글쓴거 수정하기 */
  @PostMapping("/faqModify.do")
  public void faqModify(HttpServletRequest request, HttpServletResponse response) {
    faqService.faqModify(request, response);
  }
  /* 고객센터 faq 글쓴거 삭제하기 */
  @PostMapping("/faqRemove.do")
  public void faqRemove(HttpServletRequest request, HttpServletResponse response) {
    faqService.faqRemove(request, response);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  @GetMapping("/change/record.do")
  public String changeRecord(HttpSession session, HttpServletRequest request,
      @RequestParam(value = "recordPerPage", required = false, defaultValue = "10") int recordPerPage) {
    System.out.println(recordPerPage);
    session.setAttribute("recordPerPage", recordPerPage);
    return "redirect:" + request.getHeader("referer"); // 현재 주소의 이전 주소(Referer)로 이동하시오.
  }

}
