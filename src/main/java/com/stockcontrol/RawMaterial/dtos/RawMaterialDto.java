package com.stockcontrol.RawMaterial.dtos;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RawMaterialDto {

    private UUID id;
    private String code;
    private String description;
    private TypeProduct typeProduct;
    private String barcode;
    private BigDecimal cost;
}
