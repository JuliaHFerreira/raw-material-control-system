package com.stockcontrol.RawMaterial.repositories;

import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductStructureRepository extends JpaRepository <ProductStructureModel, UUID> {

    List<ProductStructureModel> findAllByProductCode(String productCode);

    void deleteAllByProductCode(String productCode);
}
