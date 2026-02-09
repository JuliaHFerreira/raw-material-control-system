package com.stockcontrol.RawMaterial.exceptions;

public class StockUpdateException extends IllegalArgumentException {
    public StockUpdateException(String message) {
        super(message);
    }
}
