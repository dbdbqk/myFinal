package com.gdu.pupo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.QnaAttachDTO;
import com.gdu.pupo.domain.QnaDTO;

@Mapper
public interface QnaMapper {

  public List<QnaDTO> getQnaList(Map<String, Object> map);
  public List<QnaAttachDTO> getAttachList(int qnaNo);
  public int qnaAdd(QnaDTO qnaDTO);
  public int qnaAddAttach(QnaAttachDTO qnaAttachDTO);
  public int getQnaCount(Map<String, Object> map);
  public int getQnaCategoryCount(String kind);
  public int qnaHitUp(int qnaNo); 
  
  public QnaDTO getQnaByNo(int qnaNo);
  // 이전글,다음글
  public QnaDTO getPrevQna(int qnaNo);
  public QnaDTO getNextQna(int qnaNo);
  

  //display, download
  public QnaAttachDTO getQnaAttachByNo(int attachNo);
  
  // download
  public int qnaDownloadCount(int attachNo);
  
 
}
