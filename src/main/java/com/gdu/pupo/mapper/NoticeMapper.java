package com.gdu.pupo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.NoticeAttachDTO;
import com.gdu.pupo.domain.NoticeDTO;

;

@Mapper
public interface NoticeMapper {

    public List<NoticeDTO> getNoticeList(Map<String, Object> map);
    public int noticeAdd(NoticeDTO noticeDTO); 
    public int noticeAddAttach(NoticeAttachDTO noticeAttachDTO);
    
    
    public NoticeDTO getNoticeByNo(int noticeNo);
    public List<NoticeAttachDTO> getAttachList(int noticeNo);
    
    public int noticeHitUp(int noticeNo); 
    public int noticeRemove(int noticeNo);
    public int noticeModify(NoticeDTO noticeDTO);

  
    public int getNoticeCount(Map<String, Object> map);
    public int getNoticeCategoryCount(String kind);
    //display, download
    public NoticeAttachDTO getNoticeAttachByNo(int attachNo);
    
    // download
    public int noticeDownloadCount(int attachNo);
    
    
    // 이전글,다음글
    public NoticeDTO getPrevNotice(int noticeNo);
    public NoticeDTO getNextNotice(int noticeNo);
    
    // noticeAttachRemove
    public int noticeAttachRemove(int attachNo);
    
  
  
}
