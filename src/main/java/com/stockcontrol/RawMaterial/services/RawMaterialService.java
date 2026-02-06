package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.CodeExistException;
import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import com.stockcontrol.RawMaterial.repositories.RawMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RawMaterialService {

    final RawMaterialRepository rawMaterialRepository;

    @Transactional
    public RawMaterialModel save(RawMaterialModel RawMaterialModel){
        if(rawMaterialRepository.existsByCode(RawMaterialModel.getCode())){
            throw new CodeExistException("The code already exists: " + RawMaterialModel.getCode() + ". Enter a new code.");
        }
        if(RawMaterialModel.getTypeProduct()== TypeProduct.FP){
            throw new IllegalArgumentException("The product is not RAW");
        }
        if(rawMaterialRepository.existsByBarcode(RawMaterialModel.getBarcode())){
            throw new BarcodeExistException("The barcode already exists: " + RawMaterialModel.getBarcode() + ". Enter a new code.");
        }
        RawMaterialModel = rawMaterialRepository.save(RawMaterialModel);
        return RawMaterialModel;
    }

    @Transactional
    public Optional<RawMaterialModel> delete(Optional<RawMaterialModel> product0){
        rawMaterialRepository.delete(product0.get());
        return product0;
    }

    public List<RawMaterialModel> allProducts(){
        return rawMaterialRepository.findAll();
    }
}
