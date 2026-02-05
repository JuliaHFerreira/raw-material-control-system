package com.stockcontrol.RawMaterial.models;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(length = 9, unique = true)
    private String code;
    @Column(length = 90)
    private String description;
    @Column(length = 3)
    @Enumerated(EnumType.STRING)
    private TypeProduct typeProduct;
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
}
