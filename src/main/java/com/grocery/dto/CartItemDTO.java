package com.grocery.dto;

import lombok.Data;

@Data
public class CartItemDTO {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Double productPrice;
    private String productName;

}