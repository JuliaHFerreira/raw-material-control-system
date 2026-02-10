package com.stockcontrol.RawMaterial.services;

import com.stockcontrol.RawMaterial.exceptions.CodeNotFoundException;
import com.stockcontrol.RawMaterial.exceptions.LessThanZeroException;
import com.stockcontrol.RawMaterial.models.ProductModel;
import com.stockcontrol.RawMaterial.models.ProductStructureModel;
import com.stockcontrol.RawMaterial.models.RawMaterialModel;
import com.stockcontrol.RawMaterial.repositories.ProductRepository;
import com.stockcontrol.RawMaterial.repositories.ProductStructureRepository;
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
class ProductStructureServiceTest {

    @Mock
    private ProductStructureRepository productStructureRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    private ProductStructureService service;

    @BeforeEach
    void setUp() {
        service = new ProductStructureService(productStructureRepository, productRepository, rawMaterialRepository);
    }

    @Test
    void save_shouldThrowCodeNotFoundException_whenProductCodeDoesNotExist() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000001");
        ps.setRawCode("RAW000001");
        ps.setQuantity(1.0);

        when(productRepository.existsByCode("PRD000001")).thenReturn(false);

        assertThatThrownBy(() -> service.save(ps))
                .isInstanceOf(CodeNotFoundException.class)
                .hasMessageContaining("Product code not found")
                .hasMessageContaining("PRD000001");

        verify(productRepository).existsByCode("PRD000001");
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(rawMaterialRepository);
        verifyNoInteractions(productStructureRepository);
    }

    @Test
    void save_shouldThrowCodeNotFoundException_whenRawMaterialCodeDoesNotExist() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000001");
        ps.setRawCode("RAW000001");
        ps.setQuantity(1.0);

        when(productRepository.existsByCode("PRD000001")).thenReturn(true);
        when(rawMaterialRepository.existsByCode("RAW000001")).thenReturn(false);

        assertThatThrownBy(() -> service.save(ps))
                .isInstanceOf(CodeNotFoundException.class)
                .hasMessageContaining("Raw Material code not found")
                .hasMessageContaining("RAW000001");

        verify(productRepository).existsByCode("PRD000001");
        verify(rawMaterialRepository).existsByCode("RAW000001");
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(productStructureRepository);
    }

    @Test
    void save_shouldThrowLessThanZeroException_whenQuantityIsZeroOrLess() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000001");
        ps.setRawCode("RAW000001");
        ps.setQuantity(0.0);

        when(productRepository.existsByCode("PRD000001")).thenReturn(true);
        when(rawMaterialRepository.existsByCode("RAW000001")).thenReturn(true);

        assertThatThrownBy(() -> service.save(ps))
                .isInstanceOf(LessThanZeroException.class)
                .hasMessage("The quantity must be greater than zero");

        verify(productRepository).existsByCode("PRD000001");
        verify(rawMaterialRepository).existsByCode("RAW000001");
        verifyNoMoreInteractions(productRepository);
        verifyNoMoreInteractions(rawMaterialRepository);
        verifyNoInteractions(productStructureRepository);
    }

    @Test
    void save_shouldThrowCodeNotFoundException_whenProductNotFoundInFindByCodeAfterExistsCheck() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000001");
        ps.setRawCode("RAW000001");
        ps.setQuantity(1.0);

        when(productRepository.existsByCode("PRD000001")).thenReturn(true);
        when(rawMaterialRepository.existsByCode("RAW000001")).thenReturn(true);

        // Exists passed, but findByCode returns empty -> should throw (your else branch)
        when(productRepository.findByCode("PRD000001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(ps))
                .isInstanceOf(CodeNotFoundException.class)
                .hasMessage("Product code not found");

        verify(productRepository).existsByCode("PRD000001");
        verify(rawMaterialRepository).existsByCode("RAW000001");
        verify(productRepository).findByCode("PRD000001");
        verifyNoInteractions(productStructureRepository);
    }

    @Test
    void save_shouldSetDescriptionsAndPersist_whenValidAndRawMaterialFound() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000001");
        ps.setRawCode("RAW000001");
        ps.setQuantity(2.0);

        when(productRepository.existsByCode("PRD000001")).thenReturn(true);
        when(rawMaterialRepository.existsByCode("RAW000001")).thenReturn(true);

        ProductModel product = new ProductModel();
        product.setDescription("SSD SATA 480GB");
        when(productRepository.findByCode("PRD000001")).thenReturn(Optional.of(product));

        RawMaterialModel raw = new RawMaterialModel();
        raw.setDescription("NAND Flash 64Gb");
        when(rawMaterialRepository.findByCode("RAW000001")).thenReturn(Optional.of(raw));

        when(productStructureRepository.save(any(ProductStructureModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductStructureModel saved = service.save(ps);

        assertThat(saved.getDescriptionProduct()).isEqualTo("SSD SATA 480GB");
        assertThat(saved.getDescriptionRaw()).isEqualTo("NAND Flash 64Gb");

        // capture to ensure the exact object sent to repository has the computed fields
        ArgumentCaptor<ProductStructureModel> captor = ArgumentCaptor.forClass(ProductStructureModel.class);
        verify(productStructureRepository).save(captor.capture());
        ProductStructureModel toSave = captor.getValue();

        assertThat(toSave.getProductCode()).isEqualTo("PRD000001");
        assertThat(toSave.getRawCode()).isEqualTo("RAW000001");
        assertThat(toSave.getQuantity()).isEqualTo(2.0);
        assertThat(toSave.getDescriptionProduct()).isEqualTo("SSD SATA 480GB");
        assertThat(toSave.getDescriptionRaw()).isEqualTo("NAND Flash 64Gb");
    }

    @Test
    void save_shouldPersist_whenValidEvenIfRawMaterialFindByCodeReturnsEmpty() {
        ProductStructureModel ps = new ProductStructureModel();
        ps.setProductCode("PRD000002");
        ps.setRawCode("RAW999999");
        ps.setQuantity(1.0);

        when(productRepository.existsByCode("PRD000002")).thenReturn(true);
        when(rawMaterialRepository.existsByCode("RAW999999")).thenReturn(true);

        ProductModel product = new ProductModel();
        product.setDescription("Fonte ATX 500W");
        when(productRepository.findByCode("PRD000002")).thenReturn(Optional.of(product));

        // Raw material is not found during description enrichment step -> your code does NOT throw
        when(rawMaterialRepository.findByCode("RAW999999")).thenReturn(Optional.empty());

        when(productStructureRepository.save(any(ProductStructureModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductStructureModel saved = service.save(ps);

        assertThat(saved.getDescriptionProduct()).isEqualTo("Fonte ATX 500W");
        assertThat(saved.getDescriptionRaw()).isNull();

        verify(productStructureRepository).save(any(ProductStructureModel.class));
    }

    @Test
    void allStructures_shouldReturnAllStructures() {
        ProductStructureModel ps1 = new ProductStructureModel();
        ProductStructureModel ps2 = new ProductStructureModel();
        when(productStructureRepository.findAll()).thenReturn(List.of(ps1, ps2));

        List<ProductStructureModel> result = service.allStructures();

        assertThat(result).containsExactly(ps1, ps2);
        verify(productStructureRepository).findAll();
        verifyNoMoreInteractions(productStructureRepository);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(rawMaterialRepository);
    }

    @Test
    void delete_shouldDeleteAndReturnSameOptional() {
        ProductStructureModel ps = new ProductStructureModel();
        Optional<ProductStructureModel> opt = Optional.of(ps);

        Optional<ProductStructureModel> result = service.delete(opt);

        assertThat(result).isSameAs(opt);
        verify(productStructureRepository).delete(ps);
        verifyNoMoreInteractions(productStructureRepository);
    }

    @Test
    void deleteAllByCode_shouldDelegateToRepository() {
        service.deleteAllByCode("PRD000001");

        verify(productStructureRepository).deleteAllByProductCode("PRD000001");
        verifyNoMoreInteractions(productStructureRepository);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(rawMaterialRepository);
    }
}
