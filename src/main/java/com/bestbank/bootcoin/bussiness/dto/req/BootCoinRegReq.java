package com.bestbank.bootcoin.bussiness.dto.req;

import com.bestbank.commons.tipos.TipoCliente;
import com.bestbank.commons.tipos.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class BootCoinRegReq {
  @NotNull(message = "Tipo de Cliente requerido")
  private TipoCliente tipoCliente;
  
  @NotNull(message = "Tipo de Documento requerido")
  private TipoDocumento tipoDocumento;
  
  @NotEmpty(message = "Numero de Dcocumento requerido")
  private String numDocumento;
  
  @NotEmpty(message = "Nombre de Cliente es requerido")
    private String nombres;
  
  @NotEmpty(message = "Apellido de Cliente requerido")
  private String apellidos;
  
  @NotEmpty(message = "Telefono de Cliente requerido")
  private String numeroTelefono;
  
  @Email(message = "Email de Cliente requerido")
  private String emailPersona;
  
  @NotNull(message = "Es cliente Monedero")
  private Integer indMonedero; 

}
