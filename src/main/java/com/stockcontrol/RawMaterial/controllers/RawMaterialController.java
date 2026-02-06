package com.stockcontrol.RawMaterial.controllers;

import com.stockcontrol.RawMaterial.dtos.RawMaterialRecordDto;
import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import com.stockcontrol.RawMaterial.repositories.RawMaterialRepository;
import com.stockcontrol.RawMaterial.services.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class RawMaterialController {

    final RawMaterialService rawMaterialService;
    final RawMaterialRepository rawMaterialRepository;

    @PostMapping("/rawmaterial")
    @Operation(
            summary = "Create a new Raw Material."
    )
    public ResponseEntity<RawMaterialModel> saveProduct(@RequestBody @Valid RawMaterialRecordDto RawMaterialRecordDto){
        var RawMaterialModel = new RawMaterialModel();

        BeanUtils.copyProperties(RawMaterialRecordDto, RawMaterialModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialService.save(RawMaterialModel));
    }

    @GetMapping("/rawmaterial")
    @Operation(
            summary = "Search all Raw Materials"
    )
    public ResponseEntity<List<RawMaterialModel>> getAllProducts(){
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.allProducts());
    }

    @GetMapping("/rawmaterial/{code}")
    @Operation(
            summary = "Search Raw Material for code"
    )
    public ResponseEntity<Object> getProductByCode(@PathVariable(value = "code") String code){
        Optional<RawMaterialModel> product0 = rawMaterialRepository.findByCode(code);

        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Raw Material not found: " + code);
        }
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());
    }

    @PutMapping("/rawmaterial/{id}")
    @Operation(
            summary = "Update Raw Material for code"
    )
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                             @RequestBody @Valid RawMaterialRecordDto RawMaterialRecordDto ){
        Optional<RawMaterialModel> product0 = rawMaterialRepository.findById(id);
        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Raw Material not found.");
        }
        var userModel = product0.get();
        BeanUtils.copyProperties(RawMaterialRecordDto, userModel);
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialRepository.save(userModel));
    }

    @DeleteMapping("/rawmaterial/{id}")
    @Operation(
            summary = "Delete Raw Material by ID"
    )
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<RawMaterialModel> product0 = rawMaterialRepository.findById(id);

        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Raw Material not found.");
        }

        var userModel = product0.get();

        rawMaterialService.delete(product0);
        return ResponseEntity.status(HttpStatus.OK).body("Raw Material deleted sucessfully.");
    }

}
