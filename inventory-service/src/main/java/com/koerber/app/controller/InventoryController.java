package com.koerber.app.controller;

import com.koerber.app.dto.InventoryBatchResponse;
import com.koerber.app.dto.InventoryUpdateRequest;
import com.koerber.app.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryBatchResponse> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getBatches(productId));
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        inventoryService.updateInventory(request);
        return ResponseEntity.noContent().build();
    }
}

