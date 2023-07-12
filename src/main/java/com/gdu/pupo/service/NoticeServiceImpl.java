package com.gdu.pupo.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import com.gdu.pupo.domain.NoticeAttachDTO;
import com.gdu.pupo.domain.NoticeDTO;
import com.gdu.pupo.mapper.NoticeMapper;
import com.gdu.pupo.util.MyFileUtil;
import com.gdu.pupo.util.PageUtil;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {
  
  private final NoticeMapper noticeMapper;
  private final PageUtil pageUtil;
  private final MyFileUtil myFileUtil;
  
  // notice 리스트 가져오기
  @Override
  public void getNoticeList(HttpServletRequest request, Model model) {
   
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
   String orderColumn = opt4.orElse("NOTICE_NO");
   
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
   int totalRecord = noticeMapper.getNoticeCount(map);
   
   // column과 query를 이용해 검색된 레코드 개수를 구한다.
 
   int kindCount = noticeMapper.getNoticeCategoryCount(kind);

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
 
   List<NoticeDTO> noticeList = noticeMapper.getNoticeList(map);
   
   // pagination.jsp로 전달할 정보를 저장합니다.
   model.addAttribute("noticeList",  noticeList);
   model.addAttribute("pagination", pageUtil.getPagination(request.getContextPath() + "/customerCenter/notice.html?orderColumn=" + orderColumn + "&order=" + order + "&query=" + query + "&kind=" + kind));
   model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
   model.addAttribute("kindCount", kindCount);
   model.addAttribute("totalRecord", totalRecord);
   model.addAttribute("page", page);
   
 }
  
  
  // notice 글 작성
  @Transactional
  @Override
  public void noticeAdd(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
      try {
          /* Notice 테이블에 QnaDTO 넣기 */

          // 카테고리, 제목, 내용 파라미터
        String noticeCategory = multipartRequest.getParameter("noticeCategory");
        String noticeTitle = multipartRequest.getParameter("noticeTitle");
        String noticeContent = multipartRequest.getParameter("noticeContent");
          // DB로 보낼 QnaDTO 만들기
        // DB로 보낼 NoticeDTO 만들기
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNoticeCategory(noticeCategory);
        noticeDTO.setNoticeTitle(noticeTitle);
        noticeDTO.setNoticeContent(noticeContent);
        
        
        // DB로 NoticeDTO 보내기
        int addResult = noticeMapper.noticeAdd(noticeDTO);  // <selectKey>에 의해서 uploadDTO 객체의 uploadNo 필드에 UPLOAD_SEQ.NEXTVAL값이 저장된다.
        
          /* 게시글 등록 결과 처리 */

          response.setContentType("text/html; charset=UTF-8");
          PrintWriter out = response.getWriter();

          out.println("<script>");
          if (addResult == 1) {
              out.println("alert('게시글이 등록되었습니다.')");
              out.println("location.href='" + multipartRequest.getContextPath() + "/customerCenter/notice.html'");
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
                                  .size(150, 300)
                                  .toFile(thumbnail);
                      }

                      /* DB에 첨부 파일 정보 저장하기 */

                      // DB로 보낼 QnaAttachDTO 만들기
                      NoticeAttachDTO noticeAttachDTO = new NoticeAttachDTO();
                      noticeAttachDTO.setFilesystemName(filesystemName);
                      noticeAttachDTO.setHasThumbnail(hasThumbnail ? 1 : 0);
                      noticeAttachDTO.setOriginName(originName);
                      noticeAttachDTO.setPath(path);
                      noticeAttachDTO.setNoticeNo(noticeDTO.getNoticeNo());

                      // DB로 QnaAttachDTO 보내기
                      noticeMapper.noticeAddAttach(noticeAttachDTO);

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
  public void getNoticeByNo(int noticeNo, Model model) {
    noticeMapper.noticeHitUp(noticeNo);
    model.addAttribute("noticeDetail", noticeMapper.getNoticeByNo(noticeNo));
    model.addAttribute("noticeAttachList", noticeMapper.getAttachList(noticeNo));
    model.addAttribute("prevArticle",  noticeMapper.getPrevNotice(noticeNo));
    model.addAttribute("nextArticle",  noticeMapper.getNextNotice(noticeNo));
    
    System.out.println(noticeMapper.getNextNotice(noticeNo)+ "-----------------------------------------");
    System.out.println(noticeMapper.getPrevNotice(noticeNo)+ "-----------------------------------------");
    }
  
  
  @Override
  public ResponseEntity<byte[]> noticeDisplay(int attachNo) {
    
    NoticeAttachDTO noticeAttachDTO = noticeMapper.getNoticeAttachByNo(attachNo);
    
    ResponseEntity<byte[]> image = null;
    
    try {
      File thumbnail = new File(noticeAttachDTO.getPath(), "s_" + noticeAttachDTO.getFilesystemName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
    
    }
  
  @Override
  public ResponseEntity<Resource> noticeDownload(int attachNo, String userAgent) {
    
    // 다운로드 할 첨부 파일의 정보(경로, 원래 이름, 저장된 이름) 가져오기
    NoticeAttachDTO noticeAttachDTO = noticeMapper.getNoticeAttachByNo(attachNo);
    
    // 다운로드 할 첨부 파일의 File 객체 -> Resource 객체
    File file = new File(noticeAttachDTO.getPath(), noticeAttachDTO.getFilesystemName());
    Resource resource = new FileSystemResource(file);
    
    // 다운로드 할 첨부 파일의 존재 여부 확인(다운로드 실패를 반환)
    if(resource.exists() == false) {
      return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }
    
    // 다운로드 횟수 증가하기
    noticeMapper.noticeDownloadCount(attachNo);
    
    
    // 다운로드 되는 파일명(첨부 파일의 원래 이름, UserAgent(브라우저)에 따른 인코딩 세팅)
    String originName = noticeAttachDTO.getOriginName();
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
    /*
    MultiValueMap<String, String> responseHeader = new HttpHeaders();
    responseHeader.add("Content-Type", "application/octet-stream");
    responseHeader.add("Content-Disposition", "attachment; filename=" + originName);
    responseHeader.add("Content-Length", file.length() + "");
    */

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
  
  
  
  @Override
  public ResponseEntity<Resource> noticeDownloadAll(int noticeNo) {
    
    // 모든 첨부 파일을 zip 파일로 압축해서 다운로드 하는 서비스
    // com.gdu.app11.batch.RemoveTempfileScheduler에 의해서 주기적으로 zip 파일들은 삭제된다.
    
    // zip 파일이 저장될 경로
    String tempPath = myFileUtil.getTempPath();
    File dir = new File(tempPath);
    if(dir.exists() == false) {
      dir.mkdirs();
    }
    
    // zip 파일의 이름
    String tempfileName = myFileUtil.getTempfileName();
    
    // zip 파일의 File 객체
    File zfile = new File(tempPath, tempfileName);
    
    // zip 파일을 생성하기 위한 Java IO Stream 선언
    BufferedInputStream bin = null;  // 각 첨부 파일을 읽어 들이는 스트림
    ZipOutputStream zout = null;     // zip 파일을 만드는 스트림
    
    // 다운로드 할 첨부 파일들의 정보(경로, 원래 이름, 저장된 이름) 가져오기
    List<NoticeAttachDTO> attachList = noticeMapper.getAttachList(noticeNo);
    
    try {
      
      // ZipOutputStream zout 객체 생성
      zout = new ZipOutputStream(new FileOutputStream(zfile));
      
      // 첨부 파일들을 하나씩 순회하면서 읽어 들인 뒤 zip 파일에 추가하기 + 각 첨부 파일들의 다운로드 횟수 증가
      for(NoticeAttachDTO attachDTO : attachList) {
        
        // zip 파일에 추가할 첨부 파일 이름 등록(첨부 파일의 원래 이름)
        ZipEntry zipEntry = new ZipEntry(attachDTO.getOriginName());
        zout.putNextEntry(zipEntry);
        
        // zip 파일에 첨부 파일 추가
        bin = new BufferedInputStream(new FileInputStream(new File(attachDTO.getPath(), attachDTO.getFilesystemName())));
        
        // bin -> zout으로 파일 복사하기 (Java 코드)
        byte[] b = new byte[1024];  // 첨부 파일을 1KB 단위로 읽겠다.
        int readByte = 0;           // 실제로 읽어 들인 바이트 수
        while((readByte = bin.read(b)) != -1) {
          zout.write(b, 0, readByte);
        }
        bin.close();
        zout.closeEntry();
        
        // 각 첨부 파일들의 다운로드 횟수 증가
        noticeMapper.noticeDownloadCount(attachDTO.getAttachNo());
        
      }
      
      zout.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // 다운로드 할 zip 파일의 File 객체 -> Resource 객체
    Resource resource = new FileSystemResource(zfile);

    // 다운로드 응답 헤더 만들기 (Spring 코드)
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    responseHeader.setContentDisposition(ContentDisposition
                                          .attachment()
                                          .filename(tempfileName)
                                          .build());
    responseHeader.setContentLength(zfile.length());
    
    // 응답
    return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);
  }
  
  
  
  
  // 공지사항 수정하기
  @Transactional
  @Override
  public int noticeModify(MultipartHttpServletRequest multipartRequest) {
    /* Notice 테이블의 정보 수정하기 */
    
    // 제목, 내용, 업로드번호 파라미터
    String noticeTitle = multipartRequest.getParameter("noticeTitle");
    String noticeCategory = multipartRequest.getParameter("noticeCategory");
    String noticeContent = multipartRequest.getParameter("noticeContent");
    int noticeNo =Integer.parseInt(multipartRequest.getParameter("noticeNo"));
    
    // DB로 보낼 NoticeDTO 만들기
    NoticeDTO noticeDTO = new NoticeDTO();
    noticeDTO.setNoticeTitle(noticeTitle);
    noticeDTO.setNoticeCategory(noticeCategory);
    noticeDTO.setNoticeContent(noticeContent);
    noticeDTO.setNoticeNo(noticeNo);
    
    // DB로 NoticeDTO 보내기
    int modifyResult = noticeMapper.noticeModify(noticeDTO);
    
    /* NoticeAttach 테이블에 NoticeAttachDTO 넣기 */
    
    // 첨부된 파일 목록
    List<MultipartFile> files = multipartRequest.getFiles("files");  // <input type="file" name="files">

    // 첨부가 없는 경우에도 files 리스트는 비어 있지 않고,
    // [MultipartFile[field="files", filename=, contentType=application/octet-stream, size=0]] 형식으로 MultipartFile을 하나 가진 것으로 처리된다.
    
    // 첨부된 파일 목록 순회
    for(MultipartFile multipartFile : files) {
      
      // 첨부된 파일이 있는지 체크
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        
        // 예외 처리
        try {
          
          /* HDD에 첨부 파일 저장하기 */
          
          // 첨부 파일의 저장 경로
          String path = myFileUtil.getPath();
          
          // 첨부 파일의 저장 경로가 없으면 만들기
          File dir = new File(path);
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
          
          /* 썸네일(첨부 파일이 이미지인 경우에만 썸네일이 가능) */
          
          // 첨부 파일의 Content-Type 확인
          String contentType = Files.probeContentType(file.toPath());  // 이미지 파일의 Content-Type : image/jpeg, image/png, image/gif, ...
          
          // DB에 저장할 썸네일 유무 정보 처리
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          
          // 첨부 파일의 Content-Type이 이미지로 확인되면 썸네일을 만듬
          if(hasThumbnail) {
            
            // HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file)
              .size(500, 700)
              .toFile(thumbnail);
            
          }
          
          /* DB에 첨부 파일 정보 저장하기 */
          
          // DB로 보낼 AttachDTO 만들기
          NoticeAttachDTO noticeAttachDTO = new NoticeAttachDTO();
          noticeAttachDTO.setFilesystemName(filesystemName);
          noticeAttachDTO.setHasThumbnail(hasThumbnail ? 1 : 0);
          noticeAttachDTO.setOriginName(originName);
          noticeAttachDTO.setPath(path);
          noticeAttachDTO.setNoticeNo(noticeDTO.getNoticeNo());
          
          // DB로 AttachDTO 보내기
          noticeMapper.noticeAddAttach(noticeAttachDTO);
          
        } catch(Exception e) {
          e.printStackTrace();
        }
        
      }
      
    }
    
    return modifyResult;
  }
  

  
  @Override
  public int noticeRemove(int noticeNo) {
    // 삭제할 첨부 파일들의 정보
    List<NoticeAttachDTO> attachList = noticeMapper.getAttachList(noticeNo);
    
    // 첨부 파일이 있으면 삭제
    if(attachList != null && attachList.isEmpty() == false) {
      
      // 삭제할 첨부 파일들을 순회하면서 하나씩 삭제
      for(NoticeAttachDTO attachDTO : attachList) {
        
        // 삭제할 첨부 파일의 File 객체
        File file = new File(attachDTO.getPath(), attachDTO.getFilesystemName());
        
        // 첨부 파일 삭제
        if(file.exists()) {
          file.delete();
        }
        
        // 첨부 파일이 썸네일을 가지고 있다면 "s_"로 시작하는 썸네일이 함께 존재하므로 함께 제거해야 한다.
        if(attachDTO.getHasThumbnail() == 1) {
          
          // 삭제할 썸네일의 File 객체
          File thumbnail = new File(attachDTO.getPath(), "s_" + attachDTO.getFilesystemName());
          
          // 썸네일 삭제
          if(thumbnail.exists()) {
            thumbnail.delete();
          }
          
        }
        
      }
      
    }
    
    // DB에서 noticeNo값을 가지는 NOTICE 테이블의 데이터를 삭제
    // 외래키 제약조건에 의해서(ON DELETE CASCADE) NOTICE 테이블의 데이터가 삭제되면
    // NOTICE_ATTACH 테이블의 데이터도 함께 삭제된다.
    int removeResult = noticeMapper.noticeRemove(noticeNo);
    
    return removeResult;
    
  }
  
  @Override
  public int noticeAttachRemove(int attachNo) {
    
    // 삭제할 첨부 파일의 정보 가져오기
    NoticeAttachDTO attachDTO = noticeMapper.getNoticeAttachByNo(attachNo);
    
    // 첨부 파일이 있으면 삭제
    if(attachDTO != null) {
      
      // 삭제할 첨부 파일의 File 객체
      File file = new File(attachDTO.getPath(), attachDTO.getFilesystemName());
      
      // 첨부 파일 삭제
      if(file.exists()) {
        file.delete();
      }
      
      // 첨부 파일이 썸네일을 가지고 있다면 "s_"로 시작하는 썸네일이 함께 존재하므로 함께 제거해야 한다.
      if(attachDTO.getHasThumbnail() == 1) {
        
        // 삭제할 썸네일의 File 객체
        File thumbnail = new File(attachDTO.getPath(), "s_" + attachDTO.getFilesystemName());
        
        // 썸네일 삭제
        if(thumbnail.exists()) {
          thumbnail.delete();
        }
          
      }
      
    }

    // DB에서 attachNo값을 가지는 ATTACH 테이블의 데이터를 삭제
    int removeResult = noticeMapper.noticeAttachRemove(attachNo);
    
    return removeResult;
  }
  
  
  
 }
 
    

