package com.bestbank.bootcoin.bussiness.utils;

public enum EstadoTransBootCoin {
  
  COLOCADA("Colocada"),
  APROBADA("APROBADA VENDEDOR"),
  LIQUIDADA("LIQUIDADA"),
  ANULADA("CANCELADA VENDEDOR"),
  SINFONDOS("SIN FONDOS");
  
  EstadoTransBootCoin(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  private String descripcion;
  
  

}
