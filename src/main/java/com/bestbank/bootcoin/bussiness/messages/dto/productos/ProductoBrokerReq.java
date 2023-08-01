package com.bestbank.bootcoin.bussiness.messages.dto.productos;



import com.bestbank.commons.tipos.TipoProducto;
import lombok.Data;

/**
 * Clase que representa una solicitud para crear o actualizar un producto.
 */
@Data
public class ProductoBrokerReq {
  
  private String codCtrlBroker;
  
  private String codigoPersona;
  
  private TipoProducto tipoProducto;
  
  
}
