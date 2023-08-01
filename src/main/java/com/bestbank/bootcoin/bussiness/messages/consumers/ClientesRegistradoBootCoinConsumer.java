package com.bestbank.bootcoin.bussiness.messages.consumers;


import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerRes;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import com.bestbank.commons.utils.JsonUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientesRegistradoBootCoinConsumer {
  
  @Autowired
  private BootCointOperationService servOperaciones;
 
  
  @KafkaListener(topics = "bc-clientes-registrado", groupId = "group_id")
  public void recibirClientesRegistrado(String clienteBrokerRes) {
    log.info("Recibiendo Cliente Registrado");
    ClienteBrokerRes clienteResp = JsonUtils.jsonToObject(clienteBrokerRes, ClienteBrokerRes.class);
    if (!Objects.isNull(clienteResp)) {
      servOperaciones
        .putAccountRegister(clienteResp)
        .subscribe(notif -> log.info(notif.toString()));
    } else {
      log.error("Cliente es Nulo");
    }
  }
  

}
