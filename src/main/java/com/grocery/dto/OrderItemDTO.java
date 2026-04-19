package com.grocery.dto;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private Long orderId;
    private String productName;
    private Integer quantity;
    private Double price;


}

