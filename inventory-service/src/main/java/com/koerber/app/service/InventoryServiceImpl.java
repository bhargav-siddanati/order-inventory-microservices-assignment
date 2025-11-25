package com.koerber.app.service;

import com.koerber.app.dto.InventoryBatchResponse;
import com.koerber.app.dto.InventoryUpdateRequest;
import com.koerber.app.exception.NotFoundException;
import com.koerber.app.exception.ValidationException;
import com.koerber.app.model.Batch;
import com.koerber.app.model.Product;
import com.koerber.app.repository.ProductRepository;
import com.koerber.app.service.factory.InventoryHandler;
import com.koerber.app.service.factory.InventoryHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepo;
    private final InventoryHandlerFactory factory;

    @Override
    public InventoryBatchResponse getBatches(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        InventoryHandler handler = factory.getHandler(product.getType());
        List<Batch> batches = handler.sortedBatches(productId);

        List<InventoryBatchResponse.BatchDto> dtos = batches.stream()
                .map(b -> InventoryBatchResponse.BatchDto.builder()
                        .batchCode(b.getBatchCode())
                        .quantity(b.getQuantity())
                        .expiryDate(b.getExpiryDate())
                        .build())
                .toList();

        return InventoryBatchResponse.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .batches(dtos)
                .build();
    }

    @Override
    public void updateInventory(InventoryUpdateRequest request) {
        if (request.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be positive");
        }
        Product product = productRepo.findBySku(request.getSku())
                .orElseThrow(() -> new NotFoundException("Product not found: " + request.getSku()));
        InventoryHandler handler = factory.getHandler(product.getType());
        handler.deduct(request.getSku(), request.getQuantity());
    }
}

