package com.grocery.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, Object>  kafkaTemplate;

    private static final String TOPIC = "order-topic";

    public void sendOrderEvent(String message) {

        kafkaTemplate.send(TOPIC, message);
    }
}