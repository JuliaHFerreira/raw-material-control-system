package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.CodeExistException;
import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import com.stockcontrol.RawMaterial.models.StockModel;
import com.stockcontrol.RawMaterial.repositories.RawMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private StockService stockService;

    private RawMaterialService service;

    @BeforeEach
    void setUp() {
        service = new RawMaterialService(rawMaterialRepository, stockService);
    }

    @Test
    void save_shouldThrowCodeExistException_whenCodeAlreadyExists() {
        RawMaterialModel rm = new RawMaterialModel();
        rm.setCode("RAW000001");
        rm.setTypeProduct(TypeProduct.RAW);
        rm.setBarcode("7892000000001");

        when(rawMaterialRepository.existsByCode("RAW000001")).thenReturn(true);

        assertThatThrownBy(() -> service.save(rm))
                .isInstanceOf(CodeExistException.class)
                .hasMessageContaining("The code already exists")
                .hasMessageContaining("RAW000001");

        verify(rawMaterialRepository).existsByCode("RAW000001");
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldThrowIllegalArgumentException_whenTypeProductIsFp() {
        RawMaterialModel rm = new RawMaterialModel();
        rm.setCode("RAW000002");
        rm.setTypeProduct(TypeProduct.FP);
        rm.setBarcode("7892000000002");

        when(rawMaterialRepository.existsByCode("RAW000002")).thenReturn(false);

        assertThatThrownBy(() -> service.save(rm))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The product is not RAW");

        verify(rawMaterialRepository).existsByCode("RAW000002");
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldThrowBarcodeExistException_whenBarcodeAlreadyExists() {
        RawMaterialModel rm = new RawMaterialModel();
        rm.setCode("RAW000003");
        rm.setDescription("Some raw material");
        rm.setTypeProduct(TypeProduct.RAW);
        rm.setBarcode("7892000000003");

        when(rawMaterialRepository.existsByCode("RAW000003")).thenReturn(false);
        when(rawMaterialRepository.existsByBarcode("7892000000003")).thenReturn(true);

        assertThatThrownBy(() -> service.save(rm))
                .isInstanceOf(BarcodeExistException.class)
                .hasMessageContaining("The barcode already exists")
                .hasMessageContaining("7892000000003");

        verify(rawMaterialRepository).existsByCode("RAW000003");
        verify(rawMaterialRepository).existsByBarcode("7892000000003");
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldPersistRawMaterialAndCreateStock_whenValid() {
        RawMaterialModel input = new RawMaterialModel();
        input.setCode("RAW000004");
        input.setDescription("NAND Flash 64Gb");
        input.setTypeProduct(TypeProduct.RAW);
        input.setBarcode("7892000000004");

        when(rawMaterialRepository.existsByCode("RAW000004")).thenReturn(false);
        when(rawMaterialRepository.existsByBarcode("7892000000004")).thenReturn(false);

        when(rawMaterialRepository.save(input)).thenReturn(input);

        RawMaterialModel saved = service.save(input);

        assertThat(saved).isSameAs(input);

        verify(rawMaterialRepository).save(input);

        ArgumentCaptor<StockModel> stockCaptor = ArgumentCaptor.forClass(StockModel.class);
        verify(stockService).createdStock(stockCaptor.capture());

        StockModel stock = stockCaptor.getValue();
        assertThat(stock.getCode()).isEqualTo("RAW000004");
        assertThat(stock.getDescription()).isEqualTo("NAND Flash 64Gb");
        assertThat(stock.getTypeProduct()).isEqualTo(TypeProduct.RAW);
        assertThat(stock.getBarcode()).isEqualTo("7892000000004");
        assertThat(stock.getStockQuantity()).isEqualTo(0.00);

        verify(rawMaterialRepository).existsByCode("RAW000004");
        verify(rawMaterialRepository).existsByBarcode("7892000000004");
        verifyNoMoreInteractions(rawMaterialRepository);
    }

    @Test
    void delete_shouldDeleteRawMaterialAndStock_whenOptionalIsPresent() {
        RawMaterialModel rm = new RawMaterialModel();
        rm.setCode("RAW000005");
        Optional<RawMaterialModel> opt = Optional.of(rm);

        Optional<RawMaterialModel> result = service.delete(opt);

        assertThat(result).isSameAs(opt);

        verify(rawMaterialRepository).delete(rm);
        verify(stockService).delete("RAW000005");
        verifyNoMoreInteractions(rawMaterialRepository);
    }

    @Test
    void delete_shouldDoNothing_whenOptionalIsEmpty() {
        Optional<RawMaterialModel> opt = Optional.empty();

        Optional<RawMaterialModel> result = service.delete(opt);

        assertThat(result).isSameAs(opt);

        verifyNoInteractions(rawMaterialRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void allProducts_shouldReturnAllRawMaterials() {
        RawMaterialModel rm1 = new RawMaterialModel();
        RawMaterialModel rm2 = new RawMaterialModel();

        when(rawMaterialRepository.findAll()).thenReturn(List.of(rm1, rm2));

        List<RawMaterialModel> result = service.allProducts();

        assertThat(result).containsExactly(rm1, rm2);
        verify(rawMaterialRepository).findAll();
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(stockService);
    }
}
