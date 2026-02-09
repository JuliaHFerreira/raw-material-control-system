package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.CodeExistException;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.models.StockModel;
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
    final StockService stockService;

    @Transactional
    public ProductModel save(ProductModel productModel){
        if(productRepository.existsByCode(productModel.getCode())){
            throw new CodeExistException("The code already exists: " + productModel.getCode() + ". Enter a new code.");
        }
        if(productModel.getTypeProduct()== TypeProduct.RAW){
            throw new IllegalArgumentException("The product is not FP");
        }
        if(productRepository.existsByBarcode(productModel.getBarcode())){
            throw new BarcodeExistException("The Code bar already exists: " + productModel.getBarcode() + ". Enter a new code.");
        }

        productModel = productRepository.save(productModel);

        StockModel stockModel = new StockModel();
        stockModel.setCode(productModel.getCode());
        stockModel.setDescription(productModel.getDescription());
        stockModel.setTypeProduct(productModel.getTypeProduct());
        stockModel.setStockQuantity(0.00);
        stockModel.setBarcode(productModel.getBarcode());

        stockService.createdStock(stockModel);
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
