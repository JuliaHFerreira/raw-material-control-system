package com.stockcontrol.RawMaterial.controllers;

import com.stockcontrol.RawMaterial.dtos.requests.StockUpdateRecordRequest;
import com.stockcontrol.RawMaterial.models.StockModel;
import com.stockcontrol.RawMaterial.repositories.StockRepository;
import com.stockcontrol.RawMaterial.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class StockController {

    final StockRepository stockRepository;
    final StockService stockService;

    @GetMapping ("/stock")
    @Operation(
            summary = "Search all Stock"
    )
    public ResponseEntity<List<StockModel>> getAllStock(){
        return ResponseEntity.status(HttpStatus.OK).body(stockService.allStock());
    }

    @PutMapping("/stock/update/{code}")
    @Operation(
            summary = "Update Stock"
    )
    public ResponseEntity <Object> updateStock(@PathVariable(value = "code") String code,
                                               @RequestBody @Valid StockUpdateRecordRequest stockUpdateRecordRequest){
        Optional<StockModel> stock0 = stockRepository.findByCode(code);
        if(stock0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
        }
        var stockModel = stock0.get();

        stockService.updateStock(stockModel, stockUpdateRecordRequest);

        return ResponseEntity.status(HttpStatus.OK).body(stockRepository.save(stockModel));
    }

    @PutMapping("/stock/clear/{code}")
    @Operation(
            summary = "Clear Stock"
    )
    public ResponseEntity <Object> clearStock(@PathVariable(value = "code") String code){
        Optional<StockModel> stock0 = stockRepository.findByCode(code);
        if(stock0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
        }
        var stockModel = stock0.get();

        stockService.clearStock(stockModel);

        return ResponseEntity.status(HttpStatus.OK).body(stockRepository.save(stockModel));
    }
}
