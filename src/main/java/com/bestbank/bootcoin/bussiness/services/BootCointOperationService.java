package com.bestbank.bootcoin.bussiness.services;

import com.bestbank.bootcoin.bussiness.dto.req.BootCoinRegReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransAuthReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransRegReq;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinNotRes;
import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerRes;
import reactor.core.publisher.Mono;

public interface BootCointOperationService {
  
  Mono<BootCoinNotRes> postAccountRegister(BootCoinRegReq bootCoinRegReq);
  
  Mono<BootCoinNotRes> putAccountRegister(ClienteBrokerRes bootCoinRegReq);
  
  Mono<BootCoinNotRes> putProductoData(ProductoBrokerRes producto);
  
  Mono<BootCoinNotRes> postTransaccionRegister(BootCoinTransRegReq transaccionRegReq);
  
  Mono<BootCoinNotRes> putTransaccionAutorizar(String idTransaccion, 
      BootCoinTransAuthReq transaccionAuthReq);
  
  Mono<BootCoinNotRes> delTransacciones(String idTransaccion);
  
  

}
