package com.msa.example.orderservice.service;

import com.msa.example.orderservice.dto.OrderDto;
import com.msa.example.orderservice.jpa.OrderEntity;
import com.msa.example.orderservice.jpa.OrderRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity entity = mapper.map(orderDto, OrderEntity.class);

        orderRepository.save(entity);

        OrderDto dto = mapper.map(entity, OrderDto.class);

        return dto;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity entity = orderRepository.findByOrderId(orderId);

        OrderDto dto = new ModelMapper().map(entity, OrderDto.class);
        return dto;
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
