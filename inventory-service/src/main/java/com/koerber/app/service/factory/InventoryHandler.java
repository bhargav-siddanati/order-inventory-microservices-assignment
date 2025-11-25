package com.koerber.app.service.factory;

import com.koerber.app.model.Batch;

import java.util.List;

public interface InventoryHandler {
    void deduct(String sku, int quantity);
    int availableQuantity(String sku);
    List<Batch> sortedBatches(Long productId);
}
