package com.bestbank.bootcoin.expossed;

import com.bestbank.bootcoin.bussiness.dto.req.BootCoinRegReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransAuthReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransRegReq;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinNotRes;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinTransRes;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/bootcoins")
@Validated
public class BootCoinRestService {
  
  @Autowired
  private BootCointOperationService servBootCoin;
  
  @PostMapping("/cuentas")
  public Mono<BootCoinNotRes> postAccountRegister(
      @Valid @RequestBody BootCoinRegReq bootCoinRegReq) {
    return servBootCoin.postAccountRegister(bootCoinRegReq);
  }
  
  
  @PostMapping("/transacciones")
  public Mono<BootCoinNotRes> postTransaccionRegister(
      @Valid @RequestBody BootCoinTransRegReq transaccionRegReq) {
    return servBootCoin.postTransaccionRegister(transaccionRegReq);
  } 
  
  @PutMapping("/transacciones/{idTransaccion}/autorizaciones")
  public Mono<BootCoinTransRes> putTransaccionAutorizar(
      @PathVariable(name = "idTransaccion") String idTransaccion, 
      @Valid @RequestBody BootCoinTransAuthReq transaccionAuthReq) {
    return null;
  }
  
  @DeleteMapping("/transacciones/{idTransaccion}")
  public Mono<BootCoinNotRes> delTransacciones(
      @PathVariable(name = "idTransaccion") String idTransaccion) {
    return servBootCoin.delTransacciones(idTransaccion);
    
  }
  

}
