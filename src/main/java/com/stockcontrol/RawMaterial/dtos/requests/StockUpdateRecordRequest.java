package com.stockcontrol.RawMaterial.dtos.requests;

import com.stockcontrol.RawMaterial.enums.StockUpdate;

public record StockUpdateRecordRequest(String code,
                                       String barcode,
                                       Double stockQuantity,
                                       StockUpdate stockUpdate) {
}
