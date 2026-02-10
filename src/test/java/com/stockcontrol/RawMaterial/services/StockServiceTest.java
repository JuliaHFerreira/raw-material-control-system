package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.dtos.requests.StockUpdateRecordRequest;
import com.stockcontrol.RawMaterial.enums.StockUpdate;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.LessThanZeroException;
import com.stockcontrol.RawMaterial.exceptions.StockUpdateException;
import com.stockcontrol.RawMaterial.models.StockModel;
import com.stockcontrol.RawMaterial.repositories.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    private StockService service;

    @BeforeEach
    void setUp() {
        service = new StockService(stockRepository);
    }

    @Test
    void createdStock_shouldSaveStockModel() {
        StockModel stock = new StockModel();
        stock.setCode("RAW000001");

        service.createdStock(stock);

        verify(stockRepository).save(stock);
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void clearStock_shouldSetQuantityToZeroAndReturnSameObject() {
        StockModel stock = new StockModel();
        stock.setStockQuantity(50.0);

        StockModel result = service.clearStock(stock);

        assertThat(result).isSameAs(stock);
        assertThat(result.getStockQuantity()).isEqualTo(0.00);
        verifyNoInteractions(stockRepository);
    }

    @Test
    void updateStock_shouldReturnUnchangedStock_whenStockIsNotFound() {
        StockModel stock = new StockModel();
        stock.setCode("RAW000001");
        stock.setStockQuantity(7.0);

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.empty());

        StockUpdateRecordRequest req =
                new StockUpdateRecordRequest("123","198512852235", 5.0, StockUpdate.ENTRY);

        StockModel result = service.updateStock(stock, req);

        assertThat(result).isSameAs(stock);
        assertThat(result.getStockQuantity()).isEqualTo(7.0);

        verify(stockRepository).findByCode("RAW000001");
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void updateStock_shouldThrowBarcodeExistException_whenBarcodeDoesNotMatch() {
        StockModel stock = new StockModel();
        stock.setCode("RAW000001");

        StockModel existing = new StockModel();
        existing.setCode("RAW000001");
        existing.setBarcode("AAA");
        existing.setStockQuantity(10.0);

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.of(existing));

        StockUpdateRecordRequest req =
                new StockUpdateRecordRequest("BBB", "198512852235", 5.0, StockUpdate.ENTRY);

        assertThatThrownBy(() -> service.updateStock(stock, req))
                .isInstanceOf(BarcodeExistException.class)
                .hasMessage("The barcode doesn't match the one registered.");

        verify(stockRepository).findByCode("RAW000001");
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void updateStock_shouldIncreaseQuantity_whenEntry() {
        StockModel stock = new StockModel();
        stock.setCode("RAW000001");

        StockModel existing = new StockModel();
        existing.setCode("RAW000001");
        existing.setBarcode("198512852235");     // MUST match request barcode
        existing.setStockQuantity(10.0);

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.of(existing));

        StockUpdateRecordRequest req =
                new StockUpdateRecordRequest("RAW000001", "198512852235", 5.0, StockUpdate.ENTRY);

        StockModel result = service.updateStock(stock, req);

        assertThat(result).isSameAs(stock);
        assertThat(result.getStockQuantity()).isEqualTo(15.0);

        verify(stockRepository).findByCode("RAW000001");
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void updateStock_shouldDecreaseQuantity_whenOutput() {
        StockModel stock = new StockModel();
        stock.setCode("RAW000001");

        StockModel existing = new StockModel();
        existing.setCode("RAW000001");
        existing.setBarcode("198512852235");     // MUST match request barcode
        existing.setStockQuantity(10.0);

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.of(existing));

        StockUpdateRecordRequest req =
                new StockUpdateRecordRequest("RAW000001", "198512852235", 4.0, StockUpdate.OUTPUT);

        StockModel result = service.updateStock(stock, req);

        assertThat(result).isSameAs(stock);
        assertThat(result.getStockQuantity()).isEqualTo(6.0);

        verify(stockRepository).findByCode("RAW000001");
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void updateStock_shouldThrowStockUpdateException_whenUpdateTypeIsNotEntryOrOutput_ifEnumHasExtraValues() {
        StockUpdate invalid = Arrays.stream(StockUpdate.values())
                .filter(v -> v != StockUpdate.ENTRY && v != StockUpdate.OUTPUT)
                .findFirst()
                .orElse(null);

        if (invalid == null) {
            return;
        }

        StockModel stock = new StockModel();
        stock.setCode("RAW000001");

        StockModel existing = new StockModel();
        existing.setCode("RAW000001");
        existing.setBarcode("123");
        existing.setStockQuantity(10.0);

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.of(existing));

        StockUpdateRecordRequest req =
                new StockUpdateRecordRequest("123", "198512852235", 5.0, StockUpdate.ENTRY);;

        assertThatThrownBy(() -> service.updateStock(stock, req))
                .isInstanceOf(StockUpdateException.class)
                .hasMessage("Enter a valid input/output type.");

        verify(stockRepository).findByCode("RAW000001");
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void delete_shouldDeleteStock_whenFound() {
        StockModel existing = new StockModel();
        existing.setCode("RAW000001");

        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.of(existing));

        service.delete("RAW000001");

        verify(stockRepository).findByCode("RAW000001");
        verify(stockRepository).delete(existing);
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void delete_shouldDoNothing_whenNotFound() {
        when(stockRepository.findByCode("RAW000001")).thenReturn(Optional.empty());

        service.delete("RAW000001");

        verify(stockRepository).findByCode("RAW000001");
        verify(stockRepository, never()).delete(any());
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void allStock_shouldReturnAllStock() {
        StockModel s1 = new StockModel();
        StockModel s2 = new StockModel();

        when(stockRepository.findAll()).thenReturn(List.of(s1, s2));

        List<StockModel> result = service.allStock();

        assertThat(result).containsExactly(s1, s2);
        verify(stockRepository).findAll();
        verifyNoMoreInteractions(stockRepository);
    }
}