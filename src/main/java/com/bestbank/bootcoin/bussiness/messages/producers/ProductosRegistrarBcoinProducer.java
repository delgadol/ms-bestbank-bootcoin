package com.bestbank.bootcoin.bussiness.messages.producers;

import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerReq;
import com.bestbank.commons.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductosRegistrarBcoinProducer {
  
  private static final String KAFKA_ACT_REGISTRAR = "productos-registrar";
  
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  
  /**
   * Envia Datos al Topico de Registar Productos
   * 
   * @param cliente
   **/
  public void enviarProductosRegistrar(ProductoBrokerReq cliente){
    String jsonProductoBrokerReq = JsonUtils.objectToJson(cliente);
    if (jsonProductoBrokerReq.isBlank()) {
      log.info("Producto no valido");
      return;
    }
    log.info("cola productos-registrar >>" + cliente.toString());
    this.kafkaTemplate.send(KAFKA_ACT_REGISTRAR, jsonProductoBrokerReq);
    
  }

}
