package com.koerber.app;

import com.koerber.app.dto.OrderRequest;
import com.koerber.app.dto.OrderResponse;
import com.koerber.app.exception.ValidationException;
import com.koerber.app.model.Order;
import com.koerber.app.repository.OrderRepository;
import com.koerber.app.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepo;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    OrderServiceImpl service;

    @BeforeEach
    void setup() throws Exception {
        // Inject inventoryBaseUrl via reflection for test
        Field f = OrderServiceImpl.class.getDeclaredField("inventoryBaseUrl");
        f.setAccessible(true);
        f.set(service, "http://localhost:8081");
    }

    @Test
    void placeOrder_success() {
        OrderRequest req = OrderRequest.builder()
                .orderNumber("ORD-1")
                .items(List.of(new OrderRequest.Item("SKU-APPLE", 10)))
                .build();

        when(orderRepo.findByOrderNumber("ORD-1")).thenReturn(Optional.empty());
        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(restTemplate.postForEntity(anyString(), any(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        OrderResponse resp = service.placeOrder(req);

        assertEquals("CREATED", resp.getStatus());
        assertEquals(1L, resp.getOrderId());
    }

    @Test
    void placeOrder_duplicateOrderNumber_throwsValidation() {
        when(orderRepo.findByOrderNumber("ORD-1"))
                .thenReturn(Optional.of(new Order()));
        OrderRequest req = OrderRequest.builder().orderNumber("ORD-1").items(List.of()).build();
        assertThrows(ValidationException.class, () -> service.placeOrder(req));
    }
}

