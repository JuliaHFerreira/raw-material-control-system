package com.stockcontrol.RawMaterial.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductStructureRecordRequest( @NotNull Double quantity,
                                            Double loss) {
}
