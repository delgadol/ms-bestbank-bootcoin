package com.bestbank.bootcoin.bussiness.messages.dto.productos;

import com.bestbank.commons.tipos.GrupoProducto;
import com.bestbank.commons.tipos.TipoCliente;
import com.bestbank.commons.tipos.TipoProducto;
import lombok.Data;

/**
 * Clase que representa la respuesta de un producto.
 */
@Data
public class ProductoBrokerRes {
  
  private String codCtrlBroker;
  
  private String id;
  
  private GrupoProducto grupoProducto;
  
  private TipoProducto tipoProducto;
  
  private String codigoProducto;
  
  private String codigoPersona;
  
  private String estado;
  
  private TipoCliente tipoCliente;
  
  private Integer maxOperacionesMes;
  
  private Integer minDiaMesOperacion; 
  
  private Double costExtraOperacionesMes;
  

}
