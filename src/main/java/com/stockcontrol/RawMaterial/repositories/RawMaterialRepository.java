package com.stockcontrol.RawMaterial.repositories;

import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RawMaterialRepository extends JpaRepository <RawMaterialModel, UUID> {

    boolean existsByCode(String code);
    boolean existsByBarcode(String codeBar);

    Optional<RawMaterialModel> findByCode(String code);

}
