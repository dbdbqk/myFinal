package com.gdu.pupo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gdu.pupo.domain.ItemDTO;
import com.gdu.pupo.service.ItemService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/item")
@Controller
public class ItemController {

  private final ItemService itemService;
  
  // 스토어 메인
  @GetMapping("/storeList.html")
  public String storeList(Model model) {
    List<ItemDTO> itemList = itemService.getAllItems();
    model.addAttribute("itemList", itemList);
    return "item/storeList";
  }
  
  
  @GetMapping("/storeDetail.html")
  public String storeDetail(int itemId, Model model) {
    itemService.getItem(itemId, model);
    return "item/storeDetail";
  }
  
  // 판매자 페이지
  @GetMapping("/itemManage.html")
  public String Manage(Model model) {
    return "item/itemManage";
  }
  
  // 상품 등록 페이지
  @GetMapping("/itemRegister.html")
  public String itemRegist(Model model) {
    return "item/itemRegister";
  }
  
  // 상품 리스트 페이지
  @GetMapping("/itemList.html")
  public String itemList(Model model) {
    List<ItemDTO> itemList = itemService.getAllItems();
    model.addAttribute("itemList", itemList);
    return "item/itemList";
  }
  
  // 카테고리별 페이지
  
  @GetMapping("/categoryList.html")
  public String categoryList(int categoryId, Model model) {
    List<ItemDTO> categoryList = itemService.getItemsByCategoryId(categoryId);
    model.addAttribute("categoryList", categoryList);
    return "item/categoryList";
  }
  
  
  // 상품 등록
  @PostMapping("/itemRegister.do")
  public String insertItem(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) {
    int registerResult = itemService.insertItem(multipartRequest);
    redirectAttributes.addFlashAttribute("registerResult", registerResult);
    return "redirect:/item/itemList.html";
  }
  
  // 상품 상세
  @GetMapping("/itemDetail.html")
  public String itemDetail(int itemId, Model model) {
    itemService.getItem(itemId, model);
    return "item/itemDetail";
  }
  
  // 상품 이미지
  @GetMapping("/itemImgDisplay.do")
  public ResponseEntity<byte[]> itemDisplay(@RequestParam("itemId") int itemId){
    return itemService.itemImgDisplay(itemId);
  }
  
  // 상품 상세 이미지
  @GetMapping("/itemImgDetailDisplay.do")
  public ResponseEntity<byte[]> itemDetailDisplay(@RequestParam("itemId") int itemId){
    return itemService.itemImgDetailDisplay(itemId);
  }
  
  // 상품 수정 페이지
  @GetMapping("/itemModify.html")
  public String modify(int itemId, Model model) {
    itemService.getItem(itemId, model);
    return "item/itemModify";
  }
  
  // 상품 수정
  @PostMapping("/itemModify.do")
  public String itemModify(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttribute) {
    int updateResult = itemService.itemUpdate(multipartRequest);
    redirectAttribute.addFlashAttribute("updateResult", updateResult);
    return "redirect:/item/itemDetail.html?itemId=" + multipartRequest.getParameter("itemId");
  }
  
  // 상품 삭제
  @PostMapping("/itemRemove.do")
  public String remove(int itemId, RedirectAttributes redirectAttribute) {
    int deleteResult = itemService.itemDelete(itemId);
    redirectAttribute.addFlashAttribute("deleteResult", deleteResult);
    return "redirect:/item/itemList.html";
  }
  
}