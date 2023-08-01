package com.bestbank.bootcoin.bussiness.dto.res;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BootCoinNotRes implements Serializable{

  private static final long serialVersionUID = 1L;

  private String codProducto;
  
  private String mensaje;
  
  private Date fecNotificacion;


}
