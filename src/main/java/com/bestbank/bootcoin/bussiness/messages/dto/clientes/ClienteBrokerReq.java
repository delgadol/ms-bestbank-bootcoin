package com.bestbank.bootcoin.bussiness.messages.dto.clientes;


import com.bestbank.commons.tipos.TipoCliente;
import com.bestbank.commons.tipos.TipoDocumento;
import lombok.Data;

/**
 * Representa una solicitud de cliente.
 * La clase ClienteReq es utilizada para enviar información relacionada a un cliente.
 */
@Data
public class ClienteBrokerReq {
  
  private String codCtrlBroker;
  
  private String codCliente;
  
  private TipoCliente tipoCliente;
  
  private TipoDocumento tipoDocumento;
  
  private String numDocumento;
  
  private String nombres;
  
  private String apellidos;
  
  private Integer indMonedero;  
  
}
