package com.bestbank.bootcoin.bussiness.services.impl;

import com.bestbank.bootcoin.bussiness.dto.req.BootCoinRegReq;
import com.bestbank.bootcoin.bussiness.dto.req.BootCoinTransRegReq;
import com.bestbank.bootcoin.bussiness.dto.res.BootCoinNotRes;
import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerReq;
import com.bestbank.bootcoin.bussiness.messages.dto.clientes.ClienteBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerReq;
import com.bestbank.bootcoin.bussiness.messages.dto.productos.ProductoBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.dto.transacciones.InfoTransaccionBrokerDualReq;
import com.bestbank.bootcoin.bussiness.messages.dto.transacciones.TransaccionBrokerRes;
import com.bestbank.bootcoin.bussiness.messages.producers.ClientesRegistrarBcoinProducer;
import com.bestbank.bootcoin.bussiness.messages.producers.ProductosRegistrarBcoinProducer;
import com.bestbank.bootcoin.bussiness.messages.producers.TransaccionesRegistrarBcoinProducer;
import com.bestbank.bootcoin.bussiness.services.BootCointOperationService;
import com.bestbank.bootcoin.bussiness.utils.ApplicationConst;
import com.bestbank.bootcoin.bussiness.utils.EstadoTransBootCoin;
import com.bestbank.bootcoin.domain.model.BootCoinAccountData;
import com.bestbank.bootcoin.domain.model.BootCoinTransaccionData;
import com.bestbank.bootcoin.domain.repository.BootCoinTransaccionRespository;
import com.bestbank.bootcoin.domain.repository.BootCointCtrlAppRespository;
import com.bestbank.commons.tipos.ResultadoTransaccion;
import com.bestbank.commons.tipos.TipoOperacion;
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
  
  @Autowired
  private TransaccionesRegistrarBcoinProducer servMov;
  
  
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

  private InfoTransaccionBrokerDualReq transaccionMsgReq(BootCoinTransaccionData data,
      TipoOperacion tipoOperacion, Boolean onRollBak) {
    InfoTransaccionBrokerDualReq transaccion = new InfoTransaccionBrokerDualReq();
    if (tipoOperacion.equals(TipoOperacion.CARGO)) {
      if (Boolean.FALSE.equals(onRollBak)) {
        transaccion.setCodPersona(data.getCodPersonaCargo());
        transaccion.setIdProducto(data.getCodCuentaCargo());
        transaccion.setIdProducto2(data.getCodCuentaAbono());
        transaccion.setMontoOperacion(data.getMontoSoles());
        transaccion.setTipoOperacion(tipoOperacion);
        transaccion.setObservacionTransaccion(data.getId().concat("-BC"));
      }else {
        transaccion.setCodPersona(data.getCodPersonaAbono());
        transaccion.setIdProducto(data.getCodCuentaAbono());
        transaccion.setIdProducto2(data.getCodCuentaCargo());
        transaccion.setMontoOperacion(data.getMontoSoles());
        transaccion.setTipoOperacion(tipoOperacion);
        transaccion.setObservacionTransaccion(data.getId().concat("-BC"));
      }
    } else {
      transaccion.setCodPersona(data.getCodPersonaAbono());
      transaccion.setIdProducto(data.getCodCuentaBootCoinCarga());
      transaccion.setIdProducto2(data.getCodCuentaBootCoinAbono());
      transaccion.setMontoOperacion(data.getMontoBootCoin());
      transaccion.setTipoOperacion(tipoOperacion);
      transaccion.setObservacionTransaccion(data.getId().concat("-BC"));
    }
    return transaccion;
  }
  
  @Override
  public Mono<BootCoinNotRes> putTransaccionAutorizar(String idTransaccion) {
    return repoTrans.findById(idTransaccion)
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0) 
            && itemf1.getEstadoTransaccion().equals(EstadoTransBootCoin.COLOCADA))
        .flatMap(item -> {
          InfoTransaccionBrokerDualReq transaccion = 
              transaccionMsgReq(item, TipoOperacion.CARGO, false);
          transaccion.setCodCtrlBroker(
              item.getId().concat(":").concat(ApplicationConst.CN_BC_TRANS_PAGOS));
          servMov.enviarTransaccionesDualRegistrar(transaccion);
          BootCoinTransaccionData itemMod = 
              ModelMapperUtils.map(item, BootCoinTransaccionData.class);
          itemMod.setEstadoTransaccion(EstadoTransBootCoin.APROBADA);
          itemMod.setFechaAprobacion(BankFnUtils.getLegacyDateTimeNow());
          itemMod.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          return repoTrans.save(itemMod)
              .flatMap(itemDb -> 
              Mono.just(new BootCoinNotRes(itemDb.getId(), "Transaccion Colocada", 
                  BankFnUtils.getLegacyDateTimeNow())
                  ));
        })
        .switchIfEmpty( 
            Mono.just(new BootCoinNotRes(idTransaccion, "Transaccion No Encontrada", 
                BankFnUtils.getLegacyDateTimeNow())
        ));
  }
  
  
  @Override
  public Mono<BootCoinNotRes> putTransaccionPago(TransaccionBrokerRes transaccionRes) {
    return repoTrans.findById(transaccionRes.getCodCtrlBroker())
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0))
        .flatMap(item -> {
          InfoTransaccionBrokerDualReq transaccion = 
              transaccionMsgReq(item, TipoOperacion.ABONO, false);
          transaccion.setCodCtrlBroker(
              item.getId().concat(":").concat(ApplicationConst.CN_BC_TRANS_LIQUIDADOS));
          BootCoinTransaccionData itemMod = 
              ModelMapperUtils.map(item, BootCoinTransaccionData.class);
          itemMod.setIdTransaccionCargo(transaccionRes.getCodControl());
          if (transaccionRes.getResultadoTransaccion()
              .equals(ResultadoTransaccion.APROBADA)) {
            itemMod.setEstadoTransaccion(EstadoTransBootCoin.PAGADA);
            servMov.enviarTransaccionesDualRegistrar(transaccion);
          } else {
            itemMod.setEstadoTransaccion(EstadoTransBootCoin.ANULADA);
          }
          itemMod.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          return repoTrans.save(itemMod)
              .flatMap(itemDb -> 
              Mono.just(new BootCoinNotRes(itemDb.getId(), "Transaccion " + 
                  itemDb.getEstadoTransaccion().toString(), 
                  BankFnUtils.getLegacyDateTimeNow())
                  ));
        })
        .switchIfEmpty( 
            Mono.just(new BootCoinNotRes(transaccionRes.getCodCtrlBroker(), 
                "Transaccion No Encontrada", 
                BankFnUtils.getLegacyDateTimeNow())
        ));
  }

  @Override
  public Mono<BootCoinNotRes> putTransaccionLiquidacion(TransaccionBrokerRes transaccionRes) {
    return repoTrans.findById(transaccionRes.getCodCtrlBroker())
        .filter(itemf1 -> itemf1.getIndEliminado().equals(0))
        .flatMap(item -> {
          InfoTransaccionBrokerDualReq transaccion = 
              transaccionMsgReq(item, TipoOperacion.CARGO, true);
          transaccion.setCodCtrlBroker(
              item.getId().concat(":").concat(ApplicationConst.CN_BC_TRANS_ROLLBACK));
          BootCoinTransaccionData itemMod = 
              ModelMapperUtils.map(item, BootCoinTransaccionData.class);
          itemMod.setIdTransaccionAbono(transaccionRes.getCodControl());
          if (transaccionRes.getResultadoTransaccion()
              .equals(ResultadoTransaccion.APROBADA)) {
            itemMod.setEstadoTransaccion(EstadoTransBootCoin.LIQUIDADA);            
          } else {
            servMov.enviarTransaccionesDualRegistrar(transaccion);
            itemMod.setEstadoTransaccion(EstadoTransBootCoin.ANULADA);
          }
          itemMod.setFechaModificacion(BankFnUtils.getLegacyDateTimeNow());
          return repoTrans.save(itemMod)
              .flatMap(itemDb -> 
              Mono.just(new BootCoinNotRes(itemDb.getId(), "Transaccion " + 
                  itemDb.getEstadoTransaccion().toString(), 
                  BankFnUtils.getLegacyDateTimeNow())
                  ));
        })
        .switchIfEmpty( 
            Mono.just(new BootCoinNotRes(transaccionRes.getCodCtrlBroker(), 
                "Transaccion No Encontrada", 
                BankFnUtils.getLegacyDateTimeNow())
        ));
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
