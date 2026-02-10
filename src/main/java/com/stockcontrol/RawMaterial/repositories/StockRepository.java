package com.stockcontrol.RawMaterial.repositories;

import com.stockcontrol.RawMaterial.models.StockModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository <StockModel, UUID> {

    Optional<StockModel> findByCode(String code);

}
