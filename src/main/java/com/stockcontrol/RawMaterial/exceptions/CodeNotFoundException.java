package com.stockcontrol.RawMaterial.exceptions;

public class CodeNotFoundException extends RuntimeException {
    public CodeNotFoundException(String message) {
        super(message);
    }
}
