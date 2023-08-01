package com.bestbank.bootcoin.bussiness.messages.dto.clientes;


import com.bestbank.commons.tipos.TipoCliente;
import com.bestbank.commons.tipos.TipoEstadoFinaciero;
import lombok.Data;

/**
 * Clase que representa la respuesta de un cliente.
 */
@Data
public class ClienteBrokerRes {
  
  private String codCtrlBroker;
  
  private String id;
  
  private String nombres;
  
  private String apellidos;
  
  private String estado;
  
  private TipoCliente tipoCliente;
  
  private TipoEstadoFinaciero estadoFinanciero;
  
}



