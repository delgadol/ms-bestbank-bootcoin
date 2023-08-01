package com.bestbank.bootcoin.domain.model;

import com.bestbank.bootcoin.bussiness.utils.EstadoTransBootCoin;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bootcointransacciones")
@Data
public class BootCoinTransaccionData {
  
  @Id
  private String id;
  
  // cuentas a cargar //
  
  private String codPersonaCargo;
  
  private String codCuentaCargo;
  
  private String codCuentaBootCoinAbono;
  
  // cuanta a abonar //
  
  private String codPersonaAbono;
  
  private String codCuentaAbono;
  
  private String codCuentaBootCoinCarga;
  
  // Montos de Transaferencia //
  
  private Double montoBootCoin;
  
  private Double tasaCambio;
  
  private Double montoSoles;
  
  // informacion Auditoria //

  private EstadoTransBootCoin estadoTransaccion;
  
  private Date fechaCreacion;
  
  private Date fechaModificacion;
  
  private Date fechaAprobacion;
  
  private Integer indEliminado; 
  
  // id de Transacciones //
  
  private String idTransaccionCargo;
  
  private String idTransaccionAbono;
  

}
