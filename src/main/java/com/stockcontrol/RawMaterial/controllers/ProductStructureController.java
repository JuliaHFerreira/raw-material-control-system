package com.stockcontrol.RawMaterial.controllers;

import com.stockcontrol.RawMaterial.dtos.ProductStructureDto;
import com.stockcontrol.RawMaterial.dtos.requests.ProductStructureRecordRequest;
import com.stockcontrol.RawMaterial.exceptions.CodeNotFoundException;
import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import com.stockcontrol.RawMaterial.repositories.ProductStructureRepository;
import com.stockcontrol.RawMaterial.services.ProductStructureService;
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

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class ProductStructureController {

    final ProductStructureService productStructureService;
    final ProductStructureRepository productStructureRepository;


    @PostMapping("/structure/new")
    @Operation(
            summary = "Create a new structure of product."
    )
    public ResponseEntity<ProductStructureModel> saveStructure(@RequestBody @Valid ProductStructureDto productStructureDto){
        var productStructureModel = new ProductStructureModel();

        BeanUtils.copyProperties(productStructureDto, productStructureModel);
        return  ResponseEntity.status(HttpStatus.CREATED).body(productStructureService.save(productStructureModel));
    }

    @GetMapping("/structure")
    @Operation(
            summary = "Search all structures of product"
    )
    public ResponseEntity<List<ProductStructureModel>> getAllStructure(){
        return  ResponseEntity.status(HttpStatus.OK).body(productStructureService.allStructures());
    }

    @GetMapping("/structure/{productCode}")
    @Operation(
            summary = "Search all structures of product by product code"
    )
    public ResponseEntity<List<ProductStructureModel>> getStructByCode(@PathVariable(value = "productCode") String productCode){

        List<ProductStructureModel> product0 = productStructureRepository.findAllByProductCode(productCode);

        if (product0.isEmpty()) {
            throw new CodeNotFoundException("Product code not found or no structure for code");
        }

        return ResponseEntity.status(HttpStatus.OK).body(product0);
    }

    @PutMapping("/structure/edit/{id}")
    @Operation(
            summary = "Update structure of product for id"
    )
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductStructureRecordRequest productStructureRecordRequest){ //recebe o corpo da requisição
        Optional<ProductStructureModel> product0 = productStructureRepository.findById(id);
        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");
        }
        var userModel = product0.get();
        BeanUtils.copyProperties(productStructureRecordRequest, userModel);
        return ResponseEntity.status(HttpStatus.OK).body(productStructureRepository.save(userModel));
    }

    @DeleteMapping("/structure/all/{productCode}")
    @Operation(
            summary = "Delete all structure by product code "
    )
    public ResponseEntity<Object> deleteStruture(@PathVariable(value = "productCode") String productCode){
        List<ProductStructureModel> product0 = productStructureRepository.findAllByProductCode(productCode);

        if (product0.isEmpty()) {
            throw new CodeNotFoundException("No structure found for code");
        }

        productStructureService.deleteAllByCode(productCode);
        return ResponseEntity.status(HttpStatus.OK).body("Structure of product code " + productCode + " deleted sucessfully.");
    }

    @DeleteMapping("/structure/{id}")
    @Operation(
            summary = "Delete one line structure by ID"
    )
    public ResponseEntity<Object> deleteLineStructure(@PathVariable(value = "id") UUID id){
        Optional<ProductStructureModel> product0 = productStructureRepository.findById(id);

        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        var userModel = product0.get();

        productStructureService.delete(product0);
        return ResponseEntity.status(HttpStatus.OK).body("product deleted sucessfully.");
    }





}
