package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.exceptions.CodeExistException;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    final ProductRepository productRepository;

    @Transactional
    public ProductModel save(ProductModel productModel){
        if(productRepository.existsByCode(productModel.getCode())){
            throw new CodeExistException("The code already exists: " + productModel.getCode() + ". Enter a new code.");
        }
        productModel = productRepository.save(productModel);
        return productModel;
    }

    @Transactional
    public Optional<ProductModel> delete(Optional<ProductModel> product0){
        productRepository.delete(product0.get());
        return product0;
    }

    public List<ProductModel> allProducts(){
        return productRepository.findAll();
    }
}
