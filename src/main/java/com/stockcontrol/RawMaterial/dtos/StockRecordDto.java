package com.stockcontrol.RawMaterial.dtos;

import com.stockcontrol.RawMaterial.enums.TypeProduct;

public record StockRecordDto(String code,
                             String description,
                             TypeProduct typeProduct,
                             String barcode) {
}
