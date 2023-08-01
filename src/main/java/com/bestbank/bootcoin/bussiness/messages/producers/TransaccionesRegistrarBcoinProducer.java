package com.bestbank.bootcoin.bussiness.messages.producers;

import com.bestbank.bootcoin.bussiness.messages.dto.transacciones.InfoTransaccionBrokerDualReq;
import com.bestbank.commons.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransaccionesRegistrarBcoinProducer {
  
  
  private static final String KAFKA_ACT_REGISTRAR = "transacciones-dual-registrar";
  
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  
  /**
   * Envia Datos al Topico de Registar Productos
   * 
   * @param cliente
   **/
  public void enviarTransaccionesDualRegistrar(InfoTransaccionBrokerDualReq transaccion){
    String jsonProductoBrokerDualReq = JsonUtils.objectToJson(transaccion);
    if (jsonProductoBrokerDualReq.isBlank()) {
      log.info("Producto no valido");
      return;
    }
    log.info("cola productos-registrar >>" + transaccion.toString());
    this.kafkaTemplate.send(KAFKA_ACT_REGISTRAR, jsonProductoBrokerDualReq);
    
  }

}
