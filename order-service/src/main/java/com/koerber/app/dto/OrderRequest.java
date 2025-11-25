package com.koerber.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank
    private String orderNumber;
    @NotEmpty
    private List<Item> items;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Item {
        @NotBlank private String sku;
        @NotNull
        @Positive
        private Integer quantity;
    }
}
