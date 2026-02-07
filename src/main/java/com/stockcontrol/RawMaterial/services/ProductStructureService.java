package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.exceptions.CodeNotFoundException;
import com.stockcontrol.RawMaterial.exceptions.LessThanZeroException;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import com.stockcontrol.RawMaterial.repositories.ProductStructureRepository;
import com.stockcontrol.RawMaterial.repositories.RawMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductStructureService {

    final ProductStructureRepository productStructureRepository;
    final ProductRepository productRepository;
    final RawMaterialRepository rawMaterialRepository;

    @Transactional
    public ProductStructureModel save(ProductStructureModel productStructureModel){

        if(!productRepository.existsByCode(productStructureModel.getProductCode())){
            throw new CodeNotFoundException("Product code not found.: " + productStructureModel.getProductCode() + ". Enter an existing code.");
        }
        if(!rawMaterialRepository.existsByCode(productStructureModel.getRawCode())){
            throw new CodeNotFoundException("Raw Material code not found.: " + productStructureModel.getProductCode() + ". Enter an existing code.");
        }
        if(productStructureModel.getQuantity() <= 0){
            throw new LessThanZeroException("The quantity must be greater than zero");
        }

        Optional<ProductModel> byDescriptionProduct = productRepository.findByCode(productStructureModel.getProductCode());

        if(byDescriptionProduct.isPresent()){
            productStructureModel.setDescriptionProduct(byDescriptionProduct.get().getDescription());
        } else {
            throw new CodeNotFoundException("Product code not found");
        }

        Optional<RawMaterialModel> byDescriptionRaw = rawMaterialRepository.findByCode(productStructureModel.getRawCode());

        if(byDescriptionRaw.isPresent()){
            productStructureModel.setDescriptionRaw(byDescriptionRaw.get().getDescription());
        }

        productStructureModel = productStructureRepository.save(productStructureModel);
        return productStructureModel;
    }

    public List<ProductStructureModel> allStructures(){
        return productStructureRepository.findAll();
    }

    @Transactional
    public Optional<ProductStructureModel> delete(Optional<ProductStructureModel> product0){
        productStructureRepository.delete(product0.get());
        return product0;
    }

    @Transactional
    public void deleteAllByCode(String productCode){
        productStructureRepository.deleteAllByProductCode(productCode);
    }
}
