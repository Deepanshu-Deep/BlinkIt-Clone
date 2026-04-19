package com.grocery.serviceImpl;

import java.util.*;
import java.util.stream.Collectors;

import com.grocery.dto.OrderItemDTO;
import com.grocery.exception.OrderException;
import com.grocery.exception.OrderItemException;
import com.grocery.exception.ProductException;
import com.grocery.model.Order;
import com.grocery.model.OrderItem;
import com.grocery.model.Product;
import com.grocery.repository.OrderItemRepository;
import com.grocery.repository.OrderRepository;
import com.grocery.repository.ProductRepository;
import com.grocery.service.OrderItemService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;



    // Add a new order item
    @Transactional
    @Override
    public OrderItemDTO addOrderItem(OrderItemDTO dto) {

        logger.info("Adding order item for orderId: {}", dto.getOrderId());

        Order order = getOrder(dto.getOrderId());
        Product product = getProduct(dto.getProductId());

        validateOrderItem(dto);

        OrderItem orderItem = buildOrderItem(order, product, dto);

        OrderItem saved = orderItemRepository.save(orderItem);

        logger.info("OrderItem created with ID: {}", saved.getId());

        return modelMapper.map(saved, OrderItemDTO.class);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found with ID: " + orderId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found with ID: " + productId));
    }

    private void validateOrderItem(OrderItemDTO dto) {

        if (dto.getQuantity() <= 0) {
            throw new OrderItemException("Quantity must be greater than 0");
        }
    }

    private OrderItem buildOrderItem(Order order, Product product, OrderItemDTO dto) {

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());

        double price = product.getPrice();
        item.setPrice(price);

        return item;
    }


    // Get an order item by ID
    @Override
    public OrderItemDTO getOrderItemById(Long id) {

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemException("OrderItem not found with ID: " + id));

        // Convert to DTO and return
        return modelMapper.map(orderItem, OrderItemDTO.class);
    }


    // Get all order items
    @Override
    public List<OrderItemDTO> getAllOrderItems() {

        List<OrderItem> orderItems = orderItemRepository.findAll();

        return orderItems.stream()
                .map(orderItem -> modelMapper.map(orderItem, OrderItemDTO.class))
                .collect(Collectors.toList());
    }


    // Update an existing order item by ID
    @Transactional
    @Override
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO dto) {

        OrderItem existing = getOrderItemEntity(id);

        Order order = getOrder(dto.getOrderId());
        Product product = getProduct(dto.getProductId());

        validateOrderItem(dto);

        existing.setOrder(order);
        existing.setProduct(product);
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(product.getPrice());

        OrderItem updated = orderItemRepository.save(existing);

        logger.info("OrderItem updated with ID: {}", id);

        return modelMapper.map(updated, OrderItemDTO.class);
    }

    // Delete an order item by ID
    @Transactional
    @Override
    public void deleteOrderItem(Long id) {

        OrderItem item = getOrderItemEntity(id);

        orderItemRepository.delete(item);

        logger.info("OrderItem deleted with ID: {}", id);
    }

    private OrderItem getOrderItemEntity(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemException("OrderItem not found with ID: " + id));
    }

    @Override
    public Page<OrderItemDTO> getByOrderId(Long orderId, int page, int size) {

        logger.info("Fetching order items for orderId: {}", orderId);

        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found with ID: " + orderId));

        Pageable pageable = PageRequest.of(page, size);

        Page<OrderItem> items = orderItemRepository.findByOrderId(orderId, pageable);

        return items.map(item -> modelMapper.map(item, OrderItemDTO.class));
    }
}

