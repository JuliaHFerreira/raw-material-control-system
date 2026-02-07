package com.stockcontrol.RawMaterial.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "productStructure")
@Getter
@Setter
public class ProductStructureModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(length = 9)
    private String productCode;
    @Column(length = 90)
    private String descriptionProduct;
    @Column(length = 9)
    private String rawCode;
    @Column(length = 90)
    private String descriptionRaw;
    private Double quantity;
    private Double loss;

    public void setRawCode(String rawCode) {
        this.rawCode = rawCode.trim().toUpperCase();
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode.trim().toUpperCase();
    }
}
