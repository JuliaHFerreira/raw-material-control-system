package com.stockcontrol.RawMaterial.exceptions;

public class BarcodeExistException extends RuntimeException {
    public BarcodeExistException(String message) {
        super(message);
    }
}
