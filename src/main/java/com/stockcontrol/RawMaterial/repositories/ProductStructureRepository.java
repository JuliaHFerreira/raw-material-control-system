package com.stockcontrol.RawMaterial.repositories;

import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductStructureRepository extends JpaRepository <ProductStructureModel, UUID> {
}
