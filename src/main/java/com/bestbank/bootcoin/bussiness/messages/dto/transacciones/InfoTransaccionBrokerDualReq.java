package com.bestbank.bootcoin.bussiness.messages.dto.transacciones;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InfoTransaccionBrokerDualReq extends InfoTransacionBrokerReq {
  
  private String idProducto2;

}
