package com.stockcontrol.RawMaterial.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductStructureRecordDto(@NotBlank String productCode,
                                        @NotBlank String rawCode,
                                        @NotNull Double quantity,
                                        @NotNull Double loss) {
}
