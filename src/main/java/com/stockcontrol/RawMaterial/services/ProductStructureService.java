package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.exceptions.CodeNotFoundException;
import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import com.stockcontrol.RawMaterial.repositories.ProductStructureRepository;
import com.stockcontrol.RawMaterial.repositories.RawMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductStructureService {

    final ProductStructureRepository productStructureRepository;
    final ProductRepository productRepository;
    final RawMaterialRepository rawMaterialRepository;

    @Transactional
    public ProductStructureModel save(ProductStructureModel productStructureModel){
        if(productRepository.existsByCode(productStructureModel.getProductCode())){
            throw new CodeNotFoundException("Product code not found.: " + productStructureModel.getProductCode() + ". Enter an existing code.");
        }
        if(rawMaterialRepository.existsByCode(productStructureModel.getRawCode())){
            throw new CodeNotFoundException("Raw Material code not found.: " + productStructureModel.getProductCode() + ". Enter an existing code.");
        }

        //Criar exceção de não poder ter quantidade <= 0
        //Criar preenchimento da descrição da materia prima e produto

        productStructureModel = productStructureRepository.save(productStructureModel);
        return productStructureModel;
    }
}
