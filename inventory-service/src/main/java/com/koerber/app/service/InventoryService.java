package com.koerber.app.service;

import com.koerber.app.dto.InventoryBatchResponse;
import com.koerber.app.dto.InventoryUpdateRequest;

public interface InventoryService {
    InventoryBatchResponse getBatches(Long productId);
    void updateInventory(InventoryUpdateRequest request);
}
