package com.gdu.pupo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gdu.pupo.intercept.AdminCheckInterceptor;
import com.gdu.pupo.intercept.LoginCheckInterceptor;
import com.gdu.pupo.intercept.SellerCheckInterceptor;
import com.gdu.pupo.util.MyFileUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  // field
  private final LoginCheckInterceptor loginCheckInterceptor;
  private final MyFileUtil myFileUtil;
  private final AdminCheckInterceptor adminCheckInterceptor;
  private final SellerCheckInterceptor sellerCheckInterceptor;
  
  
  
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    
    registry.addInterceptor(loginCheckInterceptor)
      .addPathPatterns("/regular/regularPayPage.do", "/regular/regularMyOrder.html" ,"/regular/regularPayDetail.html", "/regular/regWriteReview.html")
      .addPathPatterns("/user/logout.do")
      .addPathPatterns("/item/addCart.do", "/item/removeCart.do")
    .addPathPatterns("/customerCenter/qna.html", "/customerCenter/voc.html");
    registry.addInterceptor(adminCheckInterceptor)
    .addPathPatterns("/regular/regularAddPage.html", "/regular/regularEditPage." ,"/regular/adminRegOrder.do", "/regular/adminRegOrder.html", "/admin/regularList.html", "admin/addCoupon.html", "admin/adminUserList.html", "admin/adminMain.html", "admin/couponList.html", "admin/regularEdit.do")
    .addPathPatterns("/customerCenter/noticeWrite.html", "/customerCenter/noticeEdit.html","/customerCenter/faqEdit.html","/customerCenter/faqWrite.html","/customerCenter/qnaDetailAdmin.html","/customerCenter/qnaListAdmin.html");
//    registry.addInterceptor(adminCheckInterceptor)
//    .addPathPatterns("/admin/**", "");
    registry.addInterceptor(sellerCheckInterceptor)
    .addPathPatterns("/seller/sellerMain.html", "/item/itemRegister.html", "item/itemRegister.do", "item/itemModify.html", "item/itemModify.do", "/item/itemRemove.html", "/item/itemManage.html", "/item/itemList.html", "item/itemDetail.html");
    
    
    //registry.addInterceptor(loginCheckInterceptor)a
    //  .addPathPatterns("/**")                  // 모든 요청
    //  .excludePathPatterns("/user/leave.do");  // 제외할 요청
      
  }
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/imageLoad/**")
      .addResourceLocations("file:" + myFileUtil.getSummernoteImagePath() + "/");
  }
  
}