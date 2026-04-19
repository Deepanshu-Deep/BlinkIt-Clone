package com.grocery.dto;

import com.grocery.enums.StatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private Long userId;
    private Double totalPrice;
    private StatusType status;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private List<OrderItemDTO> orderItems;
    private String userEmail;


}

