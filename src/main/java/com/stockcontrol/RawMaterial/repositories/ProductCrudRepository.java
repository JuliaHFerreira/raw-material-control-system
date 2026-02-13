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
              p.code AS product_code,
              p.description AS product_description,
              CASE
                WHEN COUNT(sq.raw_code) = 0 THEN 0
                ELSE MIN(
                  FLOOR(
                    sr.stock_quantity /
                    NULLIF(sq.quantity * (1 + sq.loss), 0)
                  )
                )
              END AS max_producible,
              CASE
                WHEN COUNT(sq.raw_code) = 0 THEN 0
                ELSE MIN(
                  FLOOR(
                    (sr.stock_quantity /
                    NULLIF(sq.quantity * (1 + sq.loss), 0))*p.price
                  )
                )
              END AS total_price,
              ROW_NUMBER() OVER (ORDER BY P.PRICE DESC) AS priority,
              CASE
                WHEN COUNT(sq.raw_code) = 0 THEN 'N/A'
                ELSE 'OK'
              END AS structure_status
            FROM products p
            LEFT JOIN structure_quty sq
              ON sq.product_code = p.code
            LEFT JOIN stock_raw sr
              ON sr.code = sq.raw_code
            GROUP BY p.code, p.description, P.PRICE
            ORDER BY P.PRICE DESC
        """, nativeQuery = true)
    List<ProductCapacityView> findProductionCapacity();
}