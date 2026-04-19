package com.grocery.service;

import com.grocery.dto.OrderDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderDTO getOrderById(Long id);
    Page<OrderDTO> getAllOrders(int page, int size);
    List<OrderDTO> getOrdersByUserEmail(String email);
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    OrderDTO placeOrder(Long userId);
    OrderDTO placeOrderByEmail(String email);
    OrderDTO markAsShipped(Long orderId);
    OrderDTO markAsDelivered(Long orderId);
    OrderDTO cancelOrder(Long orderId);


}

