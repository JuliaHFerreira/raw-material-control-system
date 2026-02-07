package com.stockcontrol.RawMaterial.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductStructureDto {

    private UUID id;
    private String productCode;
    private String descriptionProduct;
    private String rawCode;
    private String descriptionRaw;
    private Double quantity;
    private Double loss;
}
