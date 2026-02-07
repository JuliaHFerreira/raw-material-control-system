package com.stockcontrol.RawMaterial.dtos.requests;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRecordRequest(@NotBlank String description,
                                   @NotNull TypeProduct typeProduct,
                                   String barcode,
                                   BigDecimal price) {
}
