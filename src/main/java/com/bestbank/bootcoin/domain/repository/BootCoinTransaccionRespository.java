package com.bestbank.bootcoin.domain.repository;

import com.bestbank.bootcoin.domain.model.BootCoinTransaccionData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BootCoinTransaccionRespository extends ReactiveMongoRepository<BootCoinTransaccionData, String>{

}
