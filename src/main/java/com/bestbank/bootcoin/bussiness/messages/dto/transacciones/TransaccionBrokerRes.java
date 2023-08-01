package com.bestbank.bootcoin.bussiness.messages.dto.transacciones;


import com.bestbank.commons.tipos.ResultadoTransaccion;
import com.bestbank.commons.tipos.TipoOperacion;
import java.util.Date;
import lombok.Data;

/**
 * Clase que representa la respuesta de un saldo.
 */
@Data
public class TransaccionBrokerRes {
  
  private String codCtrlBroker;

  private String codControl;
  
  private TipoOperacion codigoOperacion;

  private String codigoProducto;  
  
  private Double montoTransaccion;
  
  private Date fechaTransaccion;
  
  private ResultadoTransaccion resultadoTransaccion;
  
  private String observacionTransaccion;
  
  private Double saldoFinal;
  
}
