package com.bestbank.bootcoin.domain.model;

import com.bestbank.commons.tipos.TipoDocumento;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bootcoinappctrl")
@Data
public class BootCoinAccountData {

  @Id
  private String id;
  
  private TipoDocumento tipoDocumento;
  
  private String numDocumento;
  
  private String numeroTelefono;
  
  private String emailPersona;
  
  private Integer indMonedero;
  
  private String codPersona;
  
  private String codProducto;
  
  private String codCuentaOperaciones;
  
  private String codInstrumento;
  
  private Integer indEliminado;
  
  private Integer indStatus;
  
  private Date fechaCreacion;
  
  private Date fechaModificacion;
  
}
