package com.koerber.app;

import com.koerber.app.dto.InventoryBatchResponse;
import com.koerber.app.dto.InventoryUpdateRequest;
import com.koerber.app.exception.ValidationException;
import com.koerber.app.model.Batch;
import com.koerber.app.model.Product;
import com.koerber.app.model.ProductType;
import com.koerber.app.repository.ProductRepository;
import com.koerber.app.service.InventoryServiceImpl;
import com.koerber.app.service.factory.InventoryHandler;
import com.koerber.app.service.factory.InventoryHandlerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    ProductRepository productRepo;
    @Mock
    InventoryHandlerFactory factory;
    @Mock
    InventoryHandler handler;
    @InjectMocks
    InventoryServiceImpl service;

    @Test
    void getBatches_returnsSortedDtos() {
        Product p = Product.builder().id(1L).sku("SKU-APPLE").name("Apple").type(ProductType.PERISHABLE).build();
        when(productRepo.findById(1L)).thenReturn(Optional.of(p));
        Batch b1 = Batch.builder().batchCode("A1").quantity(10).expiryDate(LocalDate.parse("2025-12-01")).build();
        Batch b2 = Batch.builder().batchCode("A2").quantity(20).expiryDate(LocalDate.parse("2025-12-15")).build();
        when(factory.getHandler(ProductType.PERISHABLE)).thenReturn(handler);
        when(handler.sortedBatches(1L)).thenReturn(List.of(b1, b2));

        InventoryBatchResponse resp = service.getBatches(1L);

        assertEquals("SKU-APPLE", resp.getSku());
        assertEquals(2, resp.getBatches().size());
        assertEquals("A1", resp.getBatches().get(0).getBatchCode());
    }

    @Test
    void updateInventory_validatesPositiveQuantity() {
        InventoryUpdateRequest req = new InventoryUpdateRequest("SKU-APPLE", 0);
        assertThrows(ValidationException.class, () -> service.updateInventory(req));
    }
}

