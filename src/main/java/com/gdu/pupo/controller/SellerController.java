package com.gdu.pupo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.pupo.service.AdminService;

import lombok.RequiredArgsConstructor;	

@RequiredArgsConstructor
@RequestMapping("/seller")
@Controller
public class SellerController {

  // field
  private final AdminService adminService;
    
  @GetMapping("/sellerMain.html")
  public String sellerMain(Model model) {
	  model.addAttribute("userCount", adminService.userCount());
	  model.addAttribute("sellerCount", adminService.sellerCount());
	  return "seller/sellerMain";
  }
  
}