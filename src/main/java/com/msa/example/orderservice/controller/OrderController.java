package com.msa.example.orderservice.controller;

import com.msa.example.orderservice.dto.OrderDto;
import com.msa.example.orderservice.jpa.OrderEntity;
import com.msa.example.orderservice.service.OrderService;
import com.msa.example.orderservice.vo.RequestOrder;
import com.msa.example.orderservice.vo.ResponseOrder;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@AllArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable("userId") String userId,
                                            @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto dto = mapper.map(orderDetails, OrderDto.class);

        dto.setUserId(userId);
        OrderDto orderDto = orderService.createOrder(dto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        return new ResponseEntity(responseOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(o -> {
            result.add(new ModelMapper().map(o, ResponseOrder.class));
        });

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
