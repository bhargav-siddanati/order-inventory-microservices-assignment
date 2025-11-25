package com.koerber.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class InventoryControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getInventory_returnsSeededBatches() throws Exception {
        mockMvc.perform(get("/inventory/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-APPLE"))
                .andExpect(jsonPath("$.batches[0].batchCode").value("A1"));
    }

    @Test
    void updateInventory_deductsAcrossBatches() throws Exception {
        String body = """
      {"sku":"SKU-APPLE","quantity":60}
    """;
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }
}

