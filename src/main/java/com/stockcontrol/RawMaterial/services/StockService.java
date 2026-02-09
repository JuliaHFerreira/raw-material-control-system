package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.dtos.requests.StockUpdateRecordRequest;
import com.stockcontrol.RawMaterial.enums.StockUpdate;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.LessThanZeroException;
import com.stockcontrol.RawMaterial.exceptions.StockUpdateException;
import com.stockcontrol.RawMaterial.models.StockModel;
import com.stockcontrol.RawMaterial.repositories.StockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class StockService {

    final StockRepository stockRepository;

    public void createdStock(StockModel stockModel){
        stockRepository.save(stockModel);
    }

    public StockModel clearStock(StockModel stockModel){

        stockModel.setStockQuantity(0.00);

        return stockModel;
    }

    public StockModel updateStock(StockModel stockModel, StockUpdateRecordRequest stockUpdateRecordRequest){

        Optional<StockModel> byUpStock = stockRepository.findByCode(stockModel.getCode());

        if (byUpStock.isPresent()){
            if(!byUpStock.get().getBarcode().equals(stockModel.getBarcode())){
                throw new BarcodeExistException("The barcode doesn't match the one registered.");
            }
            if (stockUpdateRecordRequest.stockQuantity() == 0.00){
                throw new LessThanZeroException("The value cannot be 0.");
            }

            if (stockUpdateRecordRequest.stockUpdate().equals(StockUpdate.ENTRY)){
                stockModel.setStockQuantity(byUpStock.get().getStockQuantity()+stockUpdateRecordRequest.stockQuantity());
            } else if (stockUpdateRecordRequest.stockUpdate().equals(StockUpdate.OUTPUT)) {
                stockModel.setStockQuantity(byUpStock.get().getStockQuantity()-stockUpdateRecordRequest.stockQuantity());
            } else {
                throw new StockUpdateException("Enter a valid input/output type.");
            }
        }
        return stockModel;
    }

    //fazer consulta de estoque

    //funcionalidades para a consulta dos produtos que podem ser produzidos com as matérias-primas disponíveis em estoque.

}
