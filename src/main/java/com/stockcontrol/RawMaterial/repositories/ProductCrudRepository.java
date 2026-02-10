package com.stockcontrol.RawMaterial.repositories;

import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.view.ProductCapacityView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ProductCrudRepository extends CrudRepository<ProductModel, UUID> {

    @Query(value = """
        WITH stock_raw AS (
          SELECT code, stock_quantity
          FROM stock
          WHERE type_product = 'RAW'
        ),
        structure_quty AS (
          SELECT
            ps.product_code,
            ps.raw_code,
            ps.quantity,
            NVL(ps.loss, 0) AS loss
          FROM product_structure ps
        )
        SELECT
          p.code AS productCode,
          p.description AS productDescription,
          CASE
            WHEN COUNT(sq.raw_code) = 0 THEN 0
            ELSE MIN(
              FLOOR(
                NVL(sr.stock_quantity, 0) /
                NULLIF(sq.quantity * (1 + sq.loss), 0)
              )
            )
          END AS maxProducible,
          CASE
            WHEN COUNT(sq.raw_code) = 0 THEN 'N/A'
            ELSE 'OK'
          END AS structureStatus
        FROM products p
        LEFT JOIN structure_quty sq
          ON sq.product_code = p.code
        LEFT JOIN stock_raw sr
          ON sr.code = sq.raw_code
        GROUP BY p.code, p.description
        ORDER BY p.code
        """, nativeQuery = true)
    List<ProductCapacityView> findProductionCapacity();
}
