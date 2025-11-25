package com.koerber.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBatchResponse {
    private Long productId;
    private String sku;
    private String productName;
    private List<BatchDto> batches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchDto {
        private String batchCode;
        private Integer quantity;
        private LocalDate expiryDate;
    }
}
