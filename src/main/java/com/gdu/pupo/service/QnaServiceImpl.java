package com.gdu.pupo.service;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.QnaAttachDTO;
import com.gdu.pupo.domain.QnaDTO;
import com.gdu.pupo.mapper.QnaMapper;
import com.gdu.pupo.util.MyFileUtil;
import com.gdu.pupo.util.PageUtil;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class QnaServiceImpl implements QnaService {
  
  private final QnaMapper qnaMapper;
  private final PageUtil pageUtil;
  private final MyFileUtil myFileUtil;

  @Transactional
  @Override
  public void qnaAdd(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
      try {
          /* Notice 테이블에 QnaDTO 넣기 */

          // 카테고리, 제목, 내용 파라미터
          String qnaCategory = multipartRequest.getParameter("qnaCategory");
          String qnaTitle = multipartRequest.getParameter("qnaTitle");
          String qnaEmail = multipartRequest.getParameter("qnaEmail");
          String qnaContent = multipartRequest.getParameter("qnaContent");

          // DB로 보낼 QnaDTO 만들기
          QnaDTO qnaDTO = new QnaDTO();
          qnaDTO.setQnaCategory(qnaCategory);
          qnaDTO.setQnaContent(qnaContent);
          qnaDTO.setQnaTitle(qnaTitle);
          qnaDTO.setQnaEmail(qnaEmail);

          int addResult = qnaMapper.qnaAdd(qnaDTO);  // <selectKey>에 의해서 QnaDTO 객체의 qnaNo 필드에 QNA_NO 값이 저장된다.

          /* 게시글 등록 결과 처리 */

          response.setContentType("text/html; charset=UTF-8");
          PrintWriter out = response.getWriter();

          out.println("<script>");
          if (addResult == 1) {
              out.println("alert('게시글이 등록되었습니다.')");
              out.println("location.href='" + multipartRequest.getContextPath() + "/customerCenter/qna.html'");
          } else {
              out.println("alert('게시글이 등록되지 않았습니다.')");
              out.println("history.back()");
          }
          out.println("</script>");
          out.flush();
          out.close();

          /* QnaAttach 테이블에 QnaAttachDTO 넣기 */

          // 첨부된 파일 목록
          List<MultipartFile> files = multipartRequest.getFiles("files");  // <input type="file" name="files">

          // 첨부된 파일 목록 순회
          for (MultipartFile multipartFile : files) {
              // 첨부된 파일이 있는지 체크
              if (multipartFile != null && !multipartFile.isEmpty()) {
                  try {
                      /* HDD에 첨부 파일 저장하기 */

                      // 첨부 파일의 저장 경로
                      String path = myFileUtil.getPath();

                      // 첨부 파일의 저장 경로가 없으면 만들기
                      File dir = new File(path);
                      if (!dir.exists()) {
                          dir.mkdirs();
                      }

                      // 첨부 파일의 원래 이름
                      String originName = multipartFile.getOriginalFilename();
                      originName = originName.substring(originName.lastIndexOf("\\") + 1);

                      // 첨부 파일의 저장 이름
                      String filesystemName = myFileUtil.getFilesystemName(originName);

                      // 첨부 파일의 File 객체 (HDD에 저장할 첨부 파일)
                      File file = new File(dir, filesystemName);

                      // 첨부 파일을 HDD에 저장
                      multipartFile.transferTo(file);

                      /* 썸네일(첨부 파일이 이미지인 경우에만 썸네일이 가능) */

                      // 첨부 파일의 Content-Type 확인
                      String contentType = Files.probeContentType(file.toPath());

                      // DB에 저장할 썸네일 유무 정보 처리
                      boolean hasThumbnail = contentType != null && contentType.startsWith("image");

                      // 첨부 파일의 Content-Type이 이미지인 경우 썸네일 생성
                      if (hasThumbnail) {
                          // HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
                          File thumbnail = new File(dir, "s_" + filesystemName);
                          Thumbnails.of(file)
                                  .size(500, 700)
                                  .toFile(thumbnail);
                      }

                      /* DB에 첨부 파일 정보 저장하기 */

                      // DB로 보낼 QnaAttachDTO 만들기
                      QnaAttachDTO qnaAttachDTO = new QnaAttachDTO();
                      qnaAttachDTO.setFilesystemName(filesystemName);
                      qnaAttachDTO.setHasThumbnail(hasThumbnail ? 1 : 0);
                      qnaAttachDTO.setOriginName(originName);
                      qnaAttachDTO.setPath(path);
                      qnaAttachDTO.setQnaNo(qnaDTO.getQnaNo());

                      // DB로 QnaAttachDTO 보내기
                      qnaMapper.qnaAddAttach(qnaAttachDTO);

                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  
  
  @Override
  public void getQnaList(HttpServletRequest request, Model model) {
  
    
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
    String orderColumn = opt4.orElse("QNA_NO");
    
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
    int totalRecord = qnaMapper.getQnaCount(map);
    
    // column과 query를 이용해 검색된 레코드 개수를 구한다.
  
    int kindCount = qnaMapper.getQnaCategoryCount(kind);

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
  
    List<QnaDTO> qnaList = qnaMapper.getQnaList(map);
    
    // pagination.jsp로 전달할 정보를 저장합니다.
    model.addAttribute("qnaList",  qnaList);
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/customerCenter/qnaList.html?orderColumn=" + orderColumn + "&order=" + order + "&query=" + query + "&kind=" + kind));
    model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
    model.addAttribute("kindCount", kindCount);
    model.addAttribute("totalRecord", totalRecord);
    model.addAttribute("page", page);
    
   
    
  }
  
  
  @Override
  public void getQnaListAdmin(HttpServletRequest request, Model model) {
  
    
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
    String orderColumn = opt4.orElse("QNA_NO");
    
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
    int totalRecord = qnaMapper.getQnaCount(map);
    
    // column과 query를 이용해 검색된 레코드 개수를 구한다.
  
    int kindCount = qnaMapper.getQnaCategoryCount(kind);

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
  
    List<QnaDTO> qnaList = qnaMapper.getQnaList(map);
    
    // pagination.jsp로 전달할 정보를 저장합니다.
    model.addAttribute("qnaList",  qnaList);
    model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/customerCenter/qnaListAdmin.html?orderColumn=" + orderColumn + "&order=" + order + "&query=" + query + "&kind=" + kind));
    model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
    model.addAttribute("kindCount", kindCount);
    model.addAttribute("totalRecord", totalRecord);
    model.addAttribute("page", page);
    
   
    
  }
  @Override
  public void getQnaByNo(int qnaNo, Model model) {
    qnaMapper.qnaHitUp(qnaNo);
    model.addAttribute("qnaDetail", qnaMapper.getQnaByNo(qnaNo));
    model.addAttribute("qnaAttachList", qnaMapper.getAttachList(qnaNo));
    model.addAttribute("prevArticle",  qnaMapper.getPrevQna(qnaNo));
    model.addAttribute("nextArticle",  qnaMapper.getNextQna(qnaNo));
    
  }
  /*
  @Override
  public int qnaModify(MultipartHttpServletRequest multipartRequest) {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int qnaRemove(int qnaNo) {
    // TODO Auto-generated method stub
    return 0;
  }
*/  
  
  @Override
  public ResponseEntity<byte[]> qnaDisplay(int attachNo) {
    
    QnaAttachDTO qnaAttachDTO = qnaMapper.getQnaAttachByNo(attachNo);
    
    ResponseEntity<byte[]> image = null;
    
    try {
      File thumbnail = new File(qnaAttachDTO.getPath(), "s_" + qnaAttachDTO.getFilesystemName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
  
  
  @Override
  public ResponseEntity<Resource> qnaDownload(int attachNo, String userAgent) {
    
    // 다운로드 할 첨부 파일의 정보(경로, 원래 이름, 저장된 이름) 가져오기
    QnaAttachDTO qnaAttachDTO = qnaMapper.getQnaAttachByNo(attachNo);
    
    // 다운로드 할 첨부 파일의 File 객체 -> Resource 객체
    File file = new File(qnaAttachDTO.getPath(), qnaAttachDTO.getFilesystemName());
    Resource resource = new FileSystemResource(file);
    
    // 다운로드 할 첨부 파일의 존재 여부 확인(다운로드 실패를 반환)
    if(resource.exists() == false) {
      return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }
    
    // 다운로드 횟수 증가하기
    qnaMapper.qnaDownloadCount(attachNo);
    
    
    // 다운로드 되는 파일명(첨부 파일의 원래 이름, UserAgent(브라우저)에 따른 인코딩 세팅)
    String originName = qnaAttachDTO.getOriginName();
    try {
      
      // IE (UserAgent에 Trident가 포함되어 있다.)
      if(userAgent.contains("Trident")) {
        originName = URLEncoder.encode(originName, "UTF-8").replace("+", " ");
      }
      // Edge (UserAgent에 Edg가 포함되어 있다.)
      else if(userAgent.contains("Edg")) {
        originName = URLEncoder.encode(originName, "UTF-8");
      }
      // Other
      else {
        originName = new String(originName.getBytes("UTF-8"), "ISO-8859-1");
      }
      
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드 응답 헤더 만들기 (Jsp/Servlet 코드)
    
   // MultiValueMap<String, String> responseHeader = new HttpHeaders();
   // responseHeader.add("Content-Type", "application/octet-stream");
   // responseHeader.add("Content-Disposition", "attachment; filename=" + originName);
   // responseHeader.add("Content-Length", file.length() + "");
    

    // 다운로드 응답 헤더 만들기 (Spring 코드)
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    responseHeader.setContentDisposition(ContentDisposition
                                          .attachment()
                                          .filename(originName)
                                          .build());
    responseHeader.setContentLength(file.length());
    
    // 응답
    return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);
  }
  
  
  

}