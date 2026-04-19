package com.grocery.controller;

import com.grocery.dto.OrderItemDTO;
import com.grocery.service.OrderItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    // ADD ORDER ITEM
    @PostMapping
    public ResponseEntity<OrderItemDTO> addOrderItem(@Valid @RequestBody OrderItemDTO dto) {

        logger.info("Adding order item for orderId: {}", dto.getOrderId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderItemService.addOrderItem(dto));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Long id) {

        logger.info("Fetching order item with id: {}", id);

        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    // GET ALL ORDER ITEMS
    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {

        logger.info("Fetching all order items");

        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    // UPDATE ORDER ITEM
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemDTO dto) {

        logger.info("Updating order item with id: {}", id);

        return ResponseEntity.ok(orderItemService.updateOrderItem(id, dto));
    }

    // DELETE ORDER ITEM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {

        logger.info("Deleting order item with id: {}", id);

        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Page<OrderItemDTO>> getItemsByOrder(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(orderItemService.getByOrderId(orderId, page, size));
    }
}