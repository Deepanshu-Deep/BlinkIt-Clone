package com.grocery.service;

import com.grocery.dto.OrderItemDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderItemService {

    OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO);
    OrderItemDTO getOrderItemById(Long id);
    List<OrderItemDTO> getAllOrderItems();
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);
    void deleteOrderItem(Long id);
    Page<OrderItemDTO> getByOrderId(Long orderId, int page, int size);

}

