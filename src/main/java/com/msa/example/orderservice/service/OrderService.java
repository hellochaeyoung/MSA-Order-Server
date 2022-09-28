package com.msa.example.orderservice.service;

import com.msa.example.orderservice.dto.OrderDto;
import com.msa.example.orderservice.jpa.OrderEntity;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
