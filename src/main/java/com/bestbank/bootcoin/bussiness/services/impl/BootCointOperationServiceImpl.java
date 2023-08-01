package com.bestbank.bootcoin.bussiness.services.impl;

import com.bestbank.bootcoin.bussiness.dto.req.BootCoinRegReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransAuthReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransRegReq;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinNotRes;
import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerReq;
import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerReq;
import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.producers.ClientesRegistrarBcoinProducer;
import com.bestbank.bootcoin.bussiness.messages.producers.ProductosRegistrarBcoinProducer;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import com.bestbank.bootcoin.bussiness.utils.ApplicationConst;
import com.bestbank.bootcoin.bussiness.utils.EstadoTransBootCoin;
import com.bestbank.bootcoin.domain.model.BootCoinAccountData;
import com.bestbank.bootcoin.domain.model.BootCoinTransaccionData;
import com.bestbank.bootcoin.domain.repository.BootCoinTransaccionRespository;
import com.bestbank.bootcoin.domain.repository.BootCointCtrlAppRespository;
import com.bestbank.commons.tipos.TipoProducto;
import com.bestbank.commons.utils.BankFnUtils;
import com.bestbank.commons.utils.ModelMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BootCointOperationServiceImpl implements BootCointOperationService{
  
  @Autowired
  private BootCoinTransaccionRespository repoTrans;
  
  @Autowired
  private BootCointCtrlAppRespository repoAppCtrl;
  
  @Autowired
  private ClientesRegistrarBcoinProducer servClientes;
  
  @Autowired
  private ProductosRegistrarBcoinProducer servProductos;

  @Override
  public Mono<BootCoinNotRes> postAccountRegister(BootCoinRegReq bootCoinRegReq) {
    BootCoinAccountData nuevaCuenta = ModelMapperUtils.map(bootCoinRegReq,
        BootCoinAccountData.class);
    nuevaCuenta.setCodInstrumento("");
    nuevaCuenta.setCodPersona("");
    nuevaCuenta.setCodProducto("");
    nuevaCuenta.setFechaCreacion(BankFnUtils.getLegacyDateTimeNow());
    nuevaCuenta.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
    nuevaCuenta.setIndEliminado(0);
    nuevaCuenta.setIndStatus(0);
    return repoAppCtrl.findFirstByTipoDocumentoAndNumDocumento(
        bootCoinRegReq.getTipoDocumento(), bootCoinRegReq.getNumDocumento())
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0))
        .flatMap(itemdb -> {
          log.info("Encontrado :: Cliente Existe");
          String msg = itemdb.getIndStatus().equals(0) ? "NORMAL" : "OBSERVADO";
          msg = itemdb.getIndEliminado().equals(1) ? "ELIMINADO" : msg;
          return Mono.just(new BootCoinNotRes(
              itemdb.getId(), msg, itemdb.getFechaCreacion()));
        })
        .switchIfEmpty(
            repoAppCtrl.save(nuevaCuenta)
            .flatMap(item -> {
              log.info("Nuevo cliente :: envio cola");
              ClienteBrokerReq clienteBkReq = ModelMapperUtils.map(bootCoinRegReq,
                  ClienteBrokerReq.class);
              clienteBkReq.setCodCtrlBroker(
                  item.getId().concat(":")
                  .concat(ApplicationConst.CN_BC_CLIENTE_REGISTRADO));
              servClientes.enviarClientesResgistrar(clienteBkReq);
              return Mono.just(new BootCoinNotRes(
                  item.getId(), "PRODUCTO CREADO - STATUS 2", item.getFechaCreacion()));
            })
        );
  }

  @Override
  public Mono<BootCoinNotRes> putAccountRegister(ClienteBrokerRes bootCoinRegReq) {
    return repoAppCtrl.findById(bootCoinRegReq.getCodCtrlBroker())
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0))
        .flatMap(itemDb -> {
          BootCoinAccountData modItem = ModelMapperUtils.map(itemDb, BootCoinAccountData.class);
          modItem.setCodPersona(bootCoinRegReq.getId());
          modItem.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          modItem.setIndStatus(1);
          return repoAppCtrl.save(modItem)
            .flatMap(numItem -> {
              ProductoBrokerReq producto = new ProductoBrokerReq();
              producto.setCodCtrlBroker(numItem.getId().concat(":")
                  .concat(ApplicationConst.CN_BC_PRODUCTO_REGISTRADO));
              producto.setCodigoPersona(numItem.getCodPersona());
              producto.setTipoProducto(TipoProducto.CTBCSOL);
              servProductos.enviarProductosRegistrar(producto);
              return Mono.just(new BootCoinNotRes(
                numItem.getId(), "PRODUCTO ACTUALIZADO - STATUS 1",
                numItem.getFechaModificacion()));
            });
        })
        .switchIfEmpty(
            Mono.just(new BootCoinNotRes(
                bootCoinRegReq.getCodCtrlBroker(), ApplicationConst.ERROR_PROD_NO_ENCONTRADO,
                BankFnUtils.getLegacyDateTimeNow())
           )
        );  
  }

  @Override
  public Mono<BootCoinNotRes> putProductoData(ProductoBrokerRes producto) {
    return repoAppCtrl.findById(producto.getCodCtrlBroker())
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0))
        .flatMap(itemDb -> {
          BootCoinAccountData modItem = ModelMapperUtils.map(itemDb, BootCoinAccountData.class);
          modItem.setCodProducto(producto.getId());
          modItem.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          modItem.setIndStatus(0);
          return repoAppCtrl.save(modItem)
            .flatMap(numItem -> 
              Mono.just(new BootCoinNotRes(
                numItem.getId(), "PRODUCTO ACTUALIZADO - STATUS 0",
                numItem.getFechaModificacion()))
            );
        })
        .switchIfEmpty(
            Mono.just(new BootCoinNotRes(
                producto.getCodCtrlBroker(), ApplicationConst.ERROR_PROD_NO_ENCONTRADO,
                BankFnUtils.getLegacyDateTimeNow())
           )
        ); 
  }
  
  private BootCoinTransaccionData getTransaccionData(
      BootCoinAccountData ctaSalida, BootCoinAccountData ctaEntrada,
      BootCoinTransRegReq transaccionRegReq ) {
    BootCoinTransaccionData transaccion = 
        new BootCoinTransaccionData();
    // cargos //
    transaccion.setCodPersonaCargo(ctaEntrada.getCodPersona());
    transaccion.setCodCuentaCargo(ctaEntrada.getCodCuentaOperaciones());
    transaccion.setCodCuentaBootCoinAbono(ctaEntrada.getCodProducto());
    // abonos //
    transaccion.setCodPersonaAbono(ctaSalida.getCodPersona());
    transaccion.setCodCuentaAbono(ctaSalida.getCodCuentaOperaciones());
    transaccion.setCodCuentaBootCoinCarga(ctaSalida.getCodProducto());
    // bootcoins //
    transaccion.setMontoBootCoin(transaccionRegReq.getMontoBootCoint());
    transaccion.setTasaCambio(transaccionRegReq.getTasaMonedaNacional());
    transaccion.setMontoSoles(transaccion.getTasaCambio() * transaccion.getMontoBootCoin());
    // auditoria //
    transaccion.setEstadoTransaccion(EstadoTransBootCoin.COLOCADA);
    transaccion.setIndEliminado(0);
    transaccion.setFechaCreacion(BankFnUtils.getLegacyDateTimeNow());
    transaccion.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
    return transaccion;
  }

  
  @Override
  public Mono<BootCoinNotRes> postTransaccionRegister(BootCoinTransRegReq transaccionRegReq) {
    return repoAppCtrl.findFirstByCodProducto(transaccionRegReq.getCodCtaBootCoinSalida())
        .flatMap(ctaSalida -> {
          return repoAppCtrl.findFirstByCodProducto(transaccionRegReq.getCodCtaBootCoinEntrada())
              .flatMap(ctaEntrada -> {
                BootCoinTransaccionData transaccion = 
                    getTransaccionData(ctaSalida, ctaEntrada, transaccionRegReq);
                return repoTrans.save(transaccion)
                    .flatMap(trasB -> Mono.just(new BootCoinNotRes(
                        trasB.getId(), "TRANSACCION COLOCADA - POR AUTORIZAR",
                        transaccion.getFechaModificacion())));
              }).switchIfEmpty(
                  Mono.just(new BootCoinNotRes(
                      transaccionRegReq.getCodCtaBootCoinEntrada(), "CTA ENTRADA DESCONOCIDA",
                      BankFnUtils.getLegacyDateTimeNow()))
                );
        })
        .switchIfEmpty(
            Mono.just(new BootCoinNotRes(
              transaccionRegReq.getCodCtaBootCoinSalida(), "CTA SALIDA DESCONOCIDA",
              BankFnUtils.getLegacyDateTimeNow()))
        );
  }

  @Override
  public Mono<BootCoinNotRes> putTransaccionAutorizar(String idTransaccion, 
      BootCoinTransAuthReq transaccionAuthReq) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Mono<BootCoinNotRes> delTransacciones(String idTransaccion) {
    return repoTrans.findById(idTransaccion)
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0) 
            && itemf1.getEstadoTransaccion().equals(EstadoTransBootCoin.COLOCADA))
        .flatMap(item -> {
          BootCoinTransaccionData data = 
              ModelMapperUtils.map(item, BootCoinTransaccionData.class);
          data.setIndEliminado(1);
          data.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          data.setEstadoTransaccion(EstadoTransBootCoin.ANULADA);
          return repoTrans.save(data)
              .flatMap(itemMod -> 
              Mono.just(new BootCoinNotRes(data.getId(), "Transaccion Eliminada", 
                  BankFnUtils.getLegacyDateTimeNow())
          ));
        })
        .switchIfEmpty( 
            Mono.just(new BootCoinNotRes(idTransaccion, "Transaccion No Encontrada", 
                BankFnUtils.getLegacyDateTimeNow())
        ));
  }

}
