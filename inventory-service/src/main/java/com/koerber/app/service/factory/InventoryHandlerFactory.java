package com.koerber.app.service.factory;

import com.koerber.app.model.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryHandlerFactory {
    private final DefaultInventoryHandler defaultHandler;

    public InventoryHandler getHandler(ProductType type) {
        // Future: return different handlers based on type
        return defaultHandler;
    }
}
