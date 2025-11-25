package com.koerber.app.service;

import com.koerber.app.dto.OrderRequest;
import com.koerber.app.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);
}

