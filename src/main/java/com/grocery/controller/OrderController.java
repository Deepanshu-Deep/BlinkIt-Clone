package com.grocery.controller;

import com.grocery.dto.OrderDTO;
import com.grocery.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // PLACE ORDER
    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(Authentication authentication) {

        String email = authentication.getName();

        logger.info("Placing order for user: {}", email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrderByEmail(email));
    }

    // GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {

        logger.info("Fetching order with id: {}", id);

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // GET ALL ORDERS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(orderService.getAllOrders(page, size));
    }

    // UPDATE ORDER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO) {

        logger.info("Updating order with id: {}", id);

        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    // DELETE ORDER
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        logger.info("Deleting order with id: {}", id);

        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }


    // SHIP ORDER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderDTO> markAsShipped(@PathVariable Long id) {

        logger.info("Marking order as shipped: {}", id);

        return ResponseEntity.ok(orderService.markAsShipped(id));
    }

    // DELIVER ORDER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deliver")
    public ResponseEntity<OrderDTO> markAsDelivered(@PathVariable Long id) {

        logger.info("Marking order as delivered: {}", id);

        return ResponseEntity.ok(orderService.markAsDelivered(id));
    }

    // CANCEL ORDER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {

        logger.info("Cancelling order: {}", id);

        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    // FETCH ORDERS AS PER USERS
    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {

        String email = authentication.getName();

        logger.info("Fetching orders for user: {}", email);

        return ResponseEntity.ok(orderService.getOrdersByUserEmail(email));
    }

    // user can only see their own order
    @GetMapping("/my/{id}")
    public ResponseEntity<OrderDTO> getMyOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        logger.info("Fetching order {} for user: {}", id, email);

        OrderDTO order = orderService.getOrderById(id);

        if (!order.getUserEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order);
    }



}