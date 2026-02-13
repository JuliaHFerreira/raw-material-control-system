package com.stockcontrol.RawMaterial.view;

public interface ProductCapacityView {
    String getProductCode();
    String getProductDescription();
    Long getMaxProducible();
    Double getTotalPrice();
    Integer getPriority();
    String getStructureStatus();
}