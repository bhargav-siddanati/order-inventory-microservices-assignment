package com.koerber.app.service;

import com.koerber.app.dto.InventoryUpdateRequest;
import com.koerber.app.dto.OrderRequest;
import com.koerber.app.dto.OrderResponse;
import com.koerber.app.exception.ValidationException;
import com.koerber.app.model.Order;
import com.koerber.app.model.OrderItem;
import com.koerber.app.model.OrderStatus;
import com.koerber.app.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final RestTemplate restTemplate;
    @Value("${inventory.base-url}")
    private String inventoryBaseUrl;

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        // Idempotency check
        orderRepo.findByOrderNumber(request.getOrderNumber()).ifPresent(o -> {
            throw new ValidationException("Order already exists: " + request.getOrderNumber());
        });

        // Check availability per item
        for (OrderRequest.Item item : request.getItems()) {
            // Query available quantity by calling Inventory GET /inventory/{productId}? via SKU
            // Since Inventory uses productId for GET, we’ll rely on update path for stock and a custom availability endpoint:
            // Alternative: call POST /inventory/update with 0 to validate or add a lightweight availability endpoint.
            // For simplicity, call a new availability endpoint; if not desired, skip and rely on update exceptions.
            // We'll rely on update exceptions here to keep within given endpoints.
            // no pre-check call; proceed to update and handle errors.
        }

        // Try to deduct inventory item-by-item; if any fails, reject the order
        for (OrderRequest.Item item : request.getItems()) {
            InventoryUpdateRequest invReq = new InventoryUpdateRequest(item.getSku(), item.getQuantity());
            try {
                restTemplate.postForEntity(inventoryBaseUrl + "/inventory/update", invReq, Void.class);
            } catch (HttpClientErrorException e) {
                // propagate validation/not-found as user-level message
                if (e.getStatusCode().is4xxClientError()) {
                    throw new ValidationException("Inventory update failed for " + item.getSku() + ": " + e.getResponseBodyAsString());
                }
                throw new RuntimeException("Inventory service error");
            } catch (ResourceAccessException e) {
                throw new RuntimeException("Inventory service unavailable");
            }
        }

        // Persist order
        Order order = Order.builder()
                .orderNumber(request.getOrderNumber())
                .status(OrderStatus.CREATED)
                .createdAt(Instant.now())
                .build();

        Order finalOrder = order;
        List<OrderItem> items = request.getItems().stream()
                .map(i -> OrderItem.builder().order(finalOrder).sku(i.getSku()).quantity(i.getQuantity()).build())
                .toList();
        order.setItems(items);

        order = orderRepo.save(order);

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .build();
    }
}

