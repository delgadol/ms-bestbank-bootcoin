package com.bestbank.bootcoin.bussiness.messages.consumers;


import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerRes;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import com.bestbank.commons.utils.JsonUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductosRegistradoBootCoinConsumer {
  
  @Autowired
  private BootCointOperationService servOperaciones;
 
/**
 * Recibe de Topico de Productos Registrados
 * 
 * @param productoBrokerRes
 */
  @KafkaListener(topics = "bc-productos-registrado", groupId = "group_id")
  public void recibirProdcutosRegistrado(String productoBrokerRes) {
    log.info("Recibiendo Producto Registrado");
    ProductoBrokerRes productoResp = 
        JsonUtils.jsonToObject(productoBrokerRes, ProductoBrokerRes.class);
    log.info(productoResp.toString());
    if (!Objects.isNull(productoResp)) {
      servOperaciones
          .putProductoData(productoResp)
          .subscribe(notif -> log.info(notif.toString()));
    } else {
      log.error("Pruducto es Nulo");
    }
  }
  

}
