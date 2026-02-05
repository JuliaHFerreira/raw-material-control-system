package com.stockcontrol.RawMaterial.dtos;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import jakarta.validation.constraints.NotBlank;

public record ProductRecordDto(@NotBlank String code,
                               @NotBlank String description,
                               @NotBlank TypeProduct typeProduct) {
}
