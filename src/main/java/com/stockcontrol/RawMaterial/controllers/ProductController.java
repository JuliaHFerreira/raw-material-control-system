package com.stockcontrol.RawMaterial.controllers;

import com.stockcontrol.RawMaterial.dtos.ProductRecordDto;
import com.stockcontrol.RawMaterial.dtos.requests.ProductRecordRequest;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import com.stockcontrol.RawMaterial.services.ProductService;
import com.stockcontrol.RawMaterial.view.ProductCapacityView;
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
public class ProductController {

    final ProductService productService;
    final ProductRepository productRepository;

    @PostMapping("/product/new")
    @Operation(
            summary = "Create a new product."
    )
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        var productModel = new ProductModel();

        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productModel));
    }

    @GetMapping("/product")
    @Operation(
            summary = "Search all products"
    )
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        return ResponseEntity.status(HttpStatus.OK).body(productService.allProducts());
    }

    @GetMapping("/product/{code}")
    @Operation(
            summary = "Search products for code"
    )
    public ResponseEntity<Object> getProductByCode(@PathVariable(value = "code") String code){
        Optional<ProductModel> product0 = productRepository.findByCode(code);

        if (product0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found: " + code);
        }
        return ResponseEntity.status(HttpStatus.OK).body(product0.get());
    }

    @GetMapping("/product/production")
    @Operation(
            summary = "Show what you have available to manufacture."
    )
    public ResponseEntity<List<ProductCapacityView>> productionAvailable(){
        return ResponseEntity.status(HttpStatus.OK).body(productService.productionvailable());
    }

    @PutMapping("/product/edit/{id}")
    @Operation(
            summary = "Update product for code"
    )
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                             @RequestBody @Valid ProductRecordRequest productRecordRequest ){ //recebe o corpo da requisição
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found.");
        }
        var userModel = product0.get();
        BeanUtils.copyProperties(productRecordRequest, userModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(userModel));
    }

    @DeleteMapping("/product/{id}")
    @Operation(
            summary = "Delete product by ID"
    )
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);

        if (product0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        var userModel = product0.get();

        productService.delete(product0);
        return ResponseEntity.status(HttpStatus.OK).body("product deleted sucessfully.");
    }

}
