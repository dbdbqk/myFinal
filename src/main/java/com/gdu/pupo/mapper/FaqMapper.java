package com.gdu.pupo.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.pupo.domain.FaqDTO;

@Mapper
public interface FaqMapper {
  
  //public List<ReviewsDTO> getReviewsList();
  public int insertFaq(FaqDTO faqDTO);
  public FaqDTO selectFaqByNo(int faqNo);
  public int faqHitUp(int faqNo);
  public int updateFaq(FaqDTO faqDTO);
  public int deleteFaq(int faqNo);
  
  
  public int getFaqCount(Map<String, Object> map);
  public List<FaqDTO> getFaqList(Map<String, Object> map);
  public int getFaqCategoryCount(String kind);
  
  public List<FaqDTO> getFaqListUsingSearch(Map<String, Object> map);
  public List<FaqDTO> getAutoComplete(Map<String, Object> map);

  

}
