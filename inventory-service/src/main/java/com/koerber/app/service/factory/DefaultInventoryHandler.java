package com.koerber.app.service.factory;

import com.koerber.app.exception.NotFoundException;
import com.koerber.app.exception.ValidationException;
import com.koerber.app.model.Batch;
import com.koerber.app.model.Product;
import com.koerber.app.repository.BatchRepository;
import com.koerber.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultInventoryHandler implements InventoryHandler {

    private final ProductRepository productRepo;
    private final BatchRepository batchRepo;

    @Override
    public void deduct(String sku, int quantity) {
        Product product = productRepo.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Product not found: " + sku));

        List<Batch> batches = batchRepo.findByProductIdOrderByExpiryDateAsc(product.getId());
        int remaining = quantity;

        for (Batch b : batches) {
            if (remaining == 0) break;
            int deduct = Math.min(b.getQuantity(), remaining);
            b.setQuantity(b.getQuantity() - deduct);
            remaining -= deduct;
        }
        if (remaining > 0) {
            throw new ValidationException("Insufficient stock for sku: " + sku);
        }
        batchRepo.saveAll(batches);
    }

    @Override
    public int availableQuantity(String sku) {
        Product product = productRepo.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Product not found: " + sku));
        return product.getBatches().stream().mapToInt(Batch::getQuantity).sum();
    }

    @Override
    public List<Batch> sortedBatches(Long productId) {
        return batchRepo.findByProductIdOrderByExpiryDateAsc(productId);
    }
}

