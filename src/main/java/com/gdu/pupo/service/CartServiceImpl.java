package com.gdu.pupo.service;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.gdu.pupo.domain.CartItemDTO;
import com.gdu.pupo.domain.ItemDTO;
import com.gdu.pupo.domain.UserDTO;
import com.gdu.pupo.mapper.CartMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

  private final CartMapper cartMapper;
    
  @Override
  public void addCart(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String id = (String) session.getAttribute("loginId");
    
    int itemId = Integer.parseInt(request.getParameter("itemId"));
    int count = Integer.parseInt(request.getParameter("count"));
    
    CartItemDTO cartItemDTO = new CartItemDTO();
    ItemDTO itemDTO = new ItemDTO();
    UserDTO userDTO = new UserDTO();
    
    itemDTO.setItemId(itemId);
    userDTO.setId(id);
    cartItemDTO.setItemDTO(itemDTO);
    cartItemDTO.setUserDTO(userDTO);
    cartItemDTO.setQuantity(count);
    
    
    cartMapper.addCart(cartItemDTO);
  }
  
  @Override
  public void getCartList(HttpServletRequest request, Model model) {
    HttpSession session = request.getSession();
    String id = (String) session.getAttribute("loginId");
    
    List<CartItemDTO> cartList = cartMapper.selectCartList(id);
    int totalPrice = 0;
    for(int i=0; i<cartList.size(); i++) {
      for(int x=0; x<cartList.get(i).getQuantity(); x++) {
        totalPrice += cartList.get(i).getItemDTO().getPrice();
      }
    }
    model.addAttribute("cartList", cartList);
    model.addAttribute("totalPrice", totalPrice);
  }

  @Override
  public void removeCart(HttpServletRequest request, HttpServletResponse response) {
      String[] cartItemIdList = request.getParameterValues("cartItemId");
      HttpSession session = request.getSession();
      String id = (String) session.getAttribute("loginId");

      try {
          if (cartItemIdList != null) {
              int removeResult = cartMapper.deleteCart(Arrays.asList(cartItemIdList));

              response.setContentType("text/html; charset=UTF-8");
              PrintWriter out = response.getWriter();
              out.println("<script>");
              if (removeResult > 0) {
                  out.println("alert('삭제되었습니다');");
                  out.println("location.href='" + request.getContextPath() + "/item/cartList.do?loginId=" + id + "';");
              } else {
                  out.println("alert('삭제되지 않았습니다');");
                  out.println("history.back();");
              }
              out.println("</script>");
              out.flush();
              out.close();
          } else {
              // 삭제할 아이템이 선택되지 않은 경우에 대한 처리
              response.setContentType("text/html; charset=UTF-8");
              PrintWriter out = response.getWriter();
              out.println("<script>");
              out.println("alert('선택된 아이템이 없습니다');");
              out.println("history.back();");
              out.println("</script>");
              out.flush();
              out.close();
          }
      } catch (Exception e) {
          e.printStackTrace();
          // TODO: 예외 처리
      }
  }

  
  
  
  /*
  @Override
  public Map<String, Object> addCart(HttpSession session, CartDTO cartDTO) {
    
    UserDTO userDTO = new UserDTO();
    userDTO.setId((String) session.getAttribute("loginId"));

    cartDTO.setId(userDTO.getId());
    
    System.out.println("장바구니 "  + cartDTO);
    
    int itemNo = cartDTO.getItemDTO().getItemId();
    int amount = cartDTO.getAmount();
    
    // 카트에 이미 담은 아이템인지 확인
    CartDTO checkCart = cartMapper.checkCart(cartDTO);
    
    int insertResult = 0;
    int updateResult = 0;
    
    // 카트에 이미 담았다면,
    if(checkCart != null) {
      // 수량을 증가시키는 mapper
      cartDTO.setAmount(amount + cartDTO.getAmount());
      System.out.println("수량 : " + cartDTO.getAmount());
     
      //updateResult = cartMapper.modifyCart(cartDTO);
      //System.out.println("결과 "  + cartMapper.modifyCart(cartDTO));
    } else {
      // 카트에 추가하는 mapper
      insertResult = cartMapper.addCart(cartDTO);
    }
    
    checkCart = cartMapper.checkCart(cartDTO);
    
    Map<String, Object> map = new HashMap<>();
    map.put("insertResult", insertResult);
    map.put("updateResult", updateResult);
    return map;
    
  }
  
  @Override
  public List<CartDTO> getCartById(HttpSession session, String id) {
    id = (String) session.getAttribute("loginId");
    return cartMapper.getCartById(id);
  }
  
  @Override
  public int deleteCart(HttpSession session, int cartId) {
    int deleteResult = cartMapper.deleteCart(cartId);
    return deleteResult;
     
  */ 
}