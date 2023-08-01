package com.bestbank.bootcoin.domain.repository;

import com.bestbank.bootcoin.domain.model.BootCoinAccountData;
import com.bestbank.commons.tipos.TipoDocumento;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import java.util.List;



public interface BootCointCtrlAppRespository extends ReactiveMongoRepository<BootCoinAccountData, String>{

    Mono<BootCoinAccountData> findFirstByTipoDocumentoAndNumDocumento(TipoDocumento tipoDocumento, String numDocumento);
    
    Mono<BootCoinAccountData> findFirstByCodProducto(String codProducto);
}
