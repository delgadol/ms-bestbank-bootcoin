package com.bestbank.bootcoin.bussiness.messages.consumers;


import com.bestbank.bootcoin.bussiness.messages.dto.transacciones.TransaccionBrokerRes;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import com.bestbank.commons.utils.JsonUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransCargoLiquidadoBootCoinConsumer {
  
  @Autowired
  private BootCointOperationService servOperaciones;
 
/**
 * Recibe de Topico de Productos Registrados
 * 
 * @param productoBrokerRes
 */
  @KafkaListener(topics = "bc-trans-cargos-registrado", groupId = "group_id")
  public void recibirTransCargosRegistrado(String transaccionBrokerRes) {
    log.info("Recibiendo Cargo Registrado - Pago en Soles");
    TransaccionBrokerRes transaccionResp = 
        JsonUtils.jsonToObject(transaccionBrokerRes, TransaccionBrokerRes.class);
    log.info(transaccionResp.toString());
    if (!Objects.isNull(transaccionResp)) {
      servOperaciones
          .putTransaccionPago(transaccionResp)
          .subscribe(notif -> log.info(notif.toString()));
    } else {
      log.error("Transaccion Pago es Nulo");
    }
  }
}
