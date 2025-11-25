package com.koerber.app.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.koerber.app.model.Batch;
import com.koerber.app.model.Product;
import com.koerber.app.model.ProductType;
import com.koerber.app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final ProductRepository productRepo;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Path path = Paths.get("src/main/resources/data/inventory-data.json");
        if (!Files.exists(path)) {
            // Try classpath
            try (InputStream is = getClass().getResourceAsStream("/data/inventory-data.json")) {
                seed(mapper.readTree(is));
            }
        } else {
            seed(mapper.readTree(Files.readString(path)));
        }
    }

    private void seed(JsonNode root) {
        ArrayNode products = (ArrayNode) root.get("products");
        for (JsonNode p : products) {
            ProductType type = ProductType.valueOf(p.get("type").asText());
            Product product = Product.builder()
                    .sku(p.get("sku").asText())
                    .name(p.get("name").asText())
                    .type(type)
                    .build();

            List<Batch> batches = new ArrayList<>();
            for (JsonNode b : (ArrayNode) p.get("batches")) {
                batches.add(Batch.builder()
                        .product(product)
                        .batchCode(b.get("batchCode").asText())
                        .quantity(b.get("quantity").asInt())
                        .expiryDate(LocalDate.parse(b.get("expiryDate").asText()))
                        .build());
            }
            product.setBatches(batches);
            productRepo.save(product);
        }
    }
}

