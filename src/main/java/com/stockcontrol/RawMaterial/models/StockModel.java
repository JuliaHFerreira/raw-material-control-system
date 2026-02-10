package com.stockcontrol.RawMaterial.models;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "stock")
@Getter
@Setter
public class StockModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(length = 9)
    private String code;
    @Column(length = 90)
    private String description;
    @Column(length = 3)
    @Enumerated(EnumType.STRING)
    private TypeProduct typeProduct;
    private Double stockQuantity;
    @Column(length = 15)
    private String barcode;

    public void setCode(String code) {
        this.code = code.trim().toUpperCase();
    }

}
