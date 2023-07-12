package com.gdu.pupo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gdu.pupo.service.CartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/item")
@Controller
public class CartController {

  private final CartService cartService;
  
  @GetMapping("/cartList.do")
  public String getCartList(HttpServletRequest request, Model model) {
	  cartService.getCartList(request, model);
	  return "item/storeCart";
  }
  
  @PostMapping("/addCart.do")
  public String addCart(HttpServletRequest request, Model model) {
	  HttpSession session = request.getSession();
	  String id = (String) session.getAttribute("loginId");
	  
	  cartService.addCart( request, model);
	  return "redirect:/item/cartList.do?loginId=" + id;
  }
  
  @PostMapping("/removeCart.do")
  public void removeCart(HttpServletRequest request, HttpServletResponse response) {
	  cartService.removeCart(request, response);
  }
  
	/*
	 * @PostMapping("/orderList.html") public String orderList(HttpServletRequest
	 * model) { cartService.getCartList(null, model)
	 * 
	 * }
	 */
  
  
  
  
	/*
	 * @ResponseBody
	 * 
	 * @PostMapping(value = "/addCart.do", produces = "application/json") public
	 * Map<String, Object> addCart(HttpSession session, @RequestBody CartDTO
	 * cartDTO) { return cartService.addCart(session, cartDTO); }
	 * 
	 * @GetMapping("/storeCart.html") public String cartPage(HttpSession session,
	 * String id, Model model) { List<CartDTO> cartInfo =
	 * cartService.getCartById(session, id); model.addAttribute("cartInfo",
	 * cartInfo); System.out.println(cartInfo); return "/item/storeCart"; }
	 * 
	 * 
	 * @ResponseBody
	 * 
	 * @PostMapping(value="/deleteCart", produces="application/json") public int
	 * deletCart(HttpSession session, @RequestBody int cartId) { return
	 * cartService.deleteCart(session, cartId); }
	 */

  /*
   * @PostMapping("/deleteCart.do") public String deletCart(HttpSession session,
   * int cartId, RedirectAttributes redirectAttributes) { int deleteResult =
   * cartService.deleteCart(session, cartId);
   * redirectAttributes.addFlashAttribute("deleteResult", deleteResult); return
   * "redirect:/item/storeCart.html"; }
   */
}
