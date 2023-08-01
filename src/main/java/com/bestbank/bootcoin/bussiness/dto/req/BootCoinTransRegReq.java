package com.bestbank.bootcoin.bussiness.dto.req;

import lombok.Data;

@Data
public class BootCoinTransRegReq {
  
  private String codCtaBootCoinSalida;
  
  private String codCtaBootCoinEntrada;
  
  private Double montoBootCoint;
  
  private Double tasaMonedaNacional; 

}
