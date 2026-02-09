package com.stockcontrol.RawMaterial.exceptions.handler;

import com.stockcontrol.RawMaterial.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CodeExistException.class)
    public ResponseEntity<String> CodeExistException(CodeExistException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BarcodeExistException.class)
    public ResponseEntity<String> CodeBarExistException(BarcodeExistException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CodeNotFoundException.class)
    public ResponseEntity<String> CodeNotFoundException(CodeNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(LessThanZeroException.class)
    public ResponseEntity<String> LessThanZeroException(LessThanZeroException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(StockUpdateException.class)
    public ResponseEntity<String> StockUpdateException(StockUpdateException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}
