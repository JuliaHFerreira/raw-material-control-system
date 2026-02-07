package com.stockcontrol.RawMaterial.exceptions;

public class LessThanZeroException extends IllegalArgumentException {
    public LessThanZeroException(String message) {
        super(message);
    }
}
