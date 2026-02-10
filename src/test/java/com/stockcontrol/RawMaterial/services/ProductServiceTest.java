package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.enums.TypeProduct;
import com.stockcontrol.RawMaterial.exceptions.BarcodeExistException;
import com.stockcontrol.RawMaterial.exceptions.CodeExistException;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.models.StockModel;
import com.stockcontrol.RawMaterial.repositories.ProductCrudRepository;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import com.stockcontrol.RawMaterial.view.ProductCapacityView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCrudRepository productCrudRepository;

    @Mock
    private StockService stockService;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productCrudRepository, stockService);
    }

    @Test
    void save_shouldThrowCodeExistException_whenCodeAlreadyExists() {
        ProductModel product = new ProductModel();
        product.setCode("PRD000001");
        product.setTypeProduct(TypeProduct.FP);
        product.setBarcode("7891000000001");

        when(productRepository.existsByCode("PRD000001")).thenReturn(true);

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(CodeExistException.class)
                .hasMessageContaining("The code already exists")
                .hasMessageContaining("PRD000001");

        verify(productRepository, times(1)).existsByCode("PRD000001");
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldThrowIllegalArgumentException_whenTypeProductIsRaw() {
        ProductModel product = new ProductModel();
        product.setCode("PRD000002");
        product.setTypeProduct(TypeProduct.RAW);
        product.setBarcode("7891000000002");

        when(productRepository.existsByCode("PRD000002")).thenReturn(false);

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The product is not FP");

        verify(productRepository, times(1)).existsByCode("PRD000002");
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldThrowBarcodeExistException_whenBarcodeAlreadyExists() {
        ProductModel product = new ProductModel();
        product.setCode("PRD000003");
        product.setTypeProduct(TypeProduct.FP);
        product.setBarcode("7891000000003");

        when(productRepository.existsByCode("PRD000003")).thenReturn(false);
        when(productRepository.existsByBarcode("7891000000003")).thenReturn(true);

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(BarcodeExistException.class)
                .hasMessageContaining("The Code bar already exists")
                .hasMessageContaining("7891000000003");

        verify(productRepository, times(1)).existsByCode("PRD000003");
        verify(productRepository, times(1)).existsByBarcode("7891000000003");
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void save_shouldPersistProductAndCreateStock_whenValid() {
        ProductModel input = new ProductModel();
        input.setCode("PRD000004");
        input.setDescription("USB Mouse");
        input.setTypeProduct(TypeProduct.FP);
        input.setBarcode("7891000000004");

        when(productRepository.existsByCode("PRD000004")).thenReturn(false);
        when(productRepository.existsByBarcode("7891000000004")).thenReturn(false);

        // Simulate repository returning the persisted entity (same instance is fine here)
        when(productRepository.save(input)).thenReturn(input);

        ProductModel saved = productService.save(input);

        assertThat(saved).isSameAs(input);

        // Verify product was saved
        verify(productRepository).save(input);

        // Verify StockModel creation and passed values
        ArgumentCaptor<StockModel> stockCaptor = ArgumentCaptor.forClass(StockModel.class);
        verify(stockService).createdStock(stockCaptor.capture());

        StockModel stock = stockCaptor.getValue();
        assertThat(stock.getCode()).isEqualTo("PRD000004");
        assertThat(stock.getDescription()).isEqualTo("USB Mouse");
        assertThat(stock.getTypeProduct()).isEqualTo(TypeProduct.FP);
        assertThat(stock.getBarcode()).isEqualTo("7891000000004");
        assertThat(stock.getStockQuantity()).isEqualTo(0.00);

        verify(productRepository).existsByCode("PRD000004");
        verify(productRepository).existsByBarcode("7891000000004");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void delete_shouldDeleteProductAndStock_whenProductIsPresent() {
        ProductModel product = new ProductModel();
        product.setCode("PRD000005");
        Optional<ProductModel> opt = Optional.of(product);

        Optional<ProductModel> result = productService.delete(opt);

        assertThat(result).isSameAs(opt);
        verify(productRepository).delete(product);
        verify(stockService).delete("PRD000005");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void delete_shouldDoNothing_whenProductIsEmpty() {
        Optional<ProductModel> opt = Optional.empty();

        Optional<ProductModel> result = productService.delete(opt);

        assertThat(result).isSameAs(opt);

        verify(productRepository, never()).delete(any());
        verify(stockService, never()).delete(anyString());
        verifyNoInteractions(productRepository);
        verifyNoInteractions(stockService);
    }

    @Test
    void allProducts_shouldReturnAllProducts() {
        List<ProductModel> products = List.of(new ProductModel(), new ProductModel());
        when(productRepository.findAll()).thenReturn(products);

        List<ProductModel> result = productService.allProducts();

        assertEquals(2, result.size());
        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void productionvailable_shouldReturnProductionCapacityViewList() {
        ProductCapacityView v1 = mock(ProductCapacityView.class);
        ProductCapacityView v2 = mock(ProductCapacityView.class);

        when(productCrudRepository.findProductionCapacity()).thenReturn(List.of(v1, v2));

        List<ProductCapacityView> result = productService.productionvailable();

        assertThat(result).containsExactly(v1, v2);
        verify(productCrudRepository).findProductionCapacity();
        verifyNoInteractions(productRepository);
        verifyNoInteractions(stockService);
    }
}
