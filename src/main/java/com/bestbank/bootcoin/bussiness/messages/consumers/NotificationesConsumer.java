package com.bestbank.bootcoin.bussiness.messages.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationesConsumer {
  
  @KafkaListener( topics = "bc-notificaciones-publicada", groupId = "group_id")
  public void regitryConsumerFn(String message) {
    log.info("==========================");
    log.info(String.format("%s", message));
    log.info("==========================");
  }
  

}
