package com.msa.example.orderservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.example.orderservice.dto.Field;
import com.msa.example.orderservice.dto.KafkaOrderDto;
import com.msa.example.orderservice.dto.OrderDto;
import com.msa.example.orderservice.dto.Payload;
import com.msa.example.orderservice.dto.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    List<Field> fields = Arrays.asList(new Field("string", true, "order_id"),
        new Field("string", true, "user_id"),
        new Field("string", true, "product_id"),
        new Field("int32", true, "qty"),
        new Field("int32", true, "unit_price"),
        new Field("int32", true, "total_price"));

    Schema schema = Schema.builder()
        .type("struct")
        .fields(fields)
        .optional(true)
        .name("orders")
        .build();

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto dto) {

        Payload payload = Payload.builder()
            .order_id(dto.getOrderId())
            .user_id(dto.getUserId())
            .product_id(dto.getProductId())
            .qty(dto.getQty())
            .unit_price(dto.getUnitPrice())
            .total_price(dto.getTotalPrice())
            .build();

        KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";

        try {
            jsonInString = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Order Producer sent data from the Order microservice: " + dto);

        return dto;
    }
}
