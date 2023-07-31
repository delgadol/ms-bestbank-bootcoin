package com.bestbank.bootcoin.expossed;

import com.bestbank.bootcoin.bussiness.dto.req.BootCoinRegReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransAuthReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransDelReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransRegReq;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinRegRes;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinTransRes;
import jakarta.validation.Valid;
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
  
  @PostMapping("/cuentas")
  public Mono<BootCoinRegRes> postAccountRegister(
      @Valid @RequestBody BootCoinRegReq bootCoinRegReq) {
    return null;
  }
  
  
  @PostMapping("/transacciones")
  public Mono<BootCoinTransRes> postTransaccionRegister(
      @Valid @RequestBody BootCoinTransRegReq transaccionRegReq) {
    return null;
  } 
  
  @PutMapping("/transacciones/{idTransaccion}/autorizaciones")
  public Mono<BootCoinTransRes> putTransaccionAutorizar(
      @PathVariable(name = "idTransaccion") String idTransaccion, 
      @Valid @RequestBody BootCoinTransAuthReq transaccionAuthReq) {
    return null;
  }
  
  @DeleteMapping("/transacciones/{idTransaccion}")
  public Mono<BootCoinTransRes> delTransacciones(
      @PathVariable(name = "idTransaccion") String idTransaccion, 
      @Valid @RequestBody BootCoinTransDelReq transaccionDelReq) {
    return null;
    
  }
  

}
