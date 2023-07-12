package com.gdu.pupo.bacth;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gdu.pupo.service.RegularService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableScheduling
@Component
public class RegularAutoPay {
  
  private final RegularService regularService;
  
  @Scheduled(cron = "0 0 0 * * ?")
  public void regAgainPay() {
    regularService.regAgainPay();
  }
  
}
