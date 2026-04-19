package com.grocery.serviceImpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.grocery.dto.OrderDTO;
import com.grocery.enums.PaymentMethodType;
import com.grocery.enums.PaymentStatusType;
import com.grocery.enums.StatusType;
import com.grocery.exception.CartException;
import com.grocery.exception.OrderException;
import com.grocery.exception.UserException;
import com.grocery.kafka.OrderProducer;
import com.grocery.model.*;
import com.grocery.repository.CartRepository;
import com.grocery.repository.OrderRepository;
import com.grocery.repository.PaymentRepository;
import com.grocery.repository.UserRepository;
import com.grocery.service.NotificationService;
import com.grocery.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderProducer orderProducer;

    // Get an order by ID
    @Override
    public OrderDTO getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with ID: " + id));

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        dto.setUserEmail(order.getUser().getEmail());

        return dto;
    }

    @Override
    public List<OrderDTO> getOrdersByUserEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();
    }

    // Get all orders
    @Override
    public Page<OrderDTO> getAllOrders(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAll(pageable);

        return orders.map(order -> modelMapper.map(order, OrderDTO.class));
    }


    // Update an existing order by ID
    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found"));

        if (orderDTO.getStatus() != null) {
            existingOrder.setStatus(orderDTO.getStatus());
        }

        Order updated = orderRepository.save(existingOrder);

        return modelMapper.map(updated, OrderDTO.class);
    }

    // Delete an order by ID
    @Override
    public void deleteOrder(Long id) {

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found with ID: " + id));

        orderRepository.delete(existingOrder);
    }

    @Override
    @Transactional
    @CacheEvict(value = "admin-dashboard", allEntries = true)
    public OrderDTO placeOrder(Long userId) {

        logger.info("Placing order for userId: {}", userId);

        User user = getUser(userId);
        Cart cart = getCart(user);

        validateCart(cart);

        Order savedOrder = createOrder(user, cart);

        createPayment(savedOrder);

        clearCart(cart);

        sendNotification(user);

        sendKafkaEvent(savedOrder);

        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = "admin-dashboard", allEntries = true)
    public OrderDTO placeOrderByEmail(String email) {

        logger.info("Placing order for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email));

        return placeOrder(user.getId());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
    }

    private Cart getCart(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found"));
    }

    private void validateCart(Cart cart) {

        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new CartException("Cart is empty or already processed");
        }
    }

    private Order createOrder(User user, Cart cart) {

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(StatusType.PAYMENT_PENDING);;

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {

            OrderItem item = new OrderItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            item.setOrder(order);

            return item;

        }).toList();

        double totalAmount = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalAmount);

        Order savedOrder = orderRepository.save(order);

        logger.info("Order created with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    private void createPayment(Order order) {

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethodType.ONLINE);
        payment.setPaymentStatus(PaymentStatusType.SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());

        paymentRepository.save(payment);

        order.setStatus(StatusType.CONFIRMED);
        orderRepository.save(order);

        logger.info("Payment successful, order confirmed for orderId: {}", order.getId());
    }

    @Override
    public OrderDTO markAsShipped(Long orderId) {

        Order order = getOrderEntity(orderId);

        if (order.getStatus() != StatusType.CONFIRMED) {
            throw new OrderException("Order must be CONFIRMED before shipping");
        }

        order.setStatus(StatusType.SHIPPED);

        orderRepository.save(order);

        logger.info("Order shipped: {}", orderId);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO markAsDelivered(Long orderId) {

        Order order = getOrderEntity(orderId);

        if (order.getStatus() != StatusType.SHIPPED) {
            throw new OrderException("Order must be SHIPPED before delivery");
        }

        order.setStatus(StatusType.DELIVERED);

        orderRepository.save(order);

        logger.info("Order delivered: {}", orderId);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderDTO cancelOrder(Long orderId) {

        Order order = getOrderEntity(orderId);

        if (order.getStatus() == StatusType.DELIVERED) {
            throw new OrderException("Delivered order cannot be cancelled");
        }

        order.setStatus(StatusType.CANCELLED);

        orderRepository.save(order);

        logger.info("Order cancelled: {}", orderId);

        return modelMapper.map(order, OrderDTO.class);
    }

    private Order getOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found"));
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartRepository.save(cart);

        logger.info("Cart cleared");
    }

    private void sendNotification(User user) {
        notificationService.sendOrderConfirmation(user.getEmail());
    }

    private void sendKafkaEvent(Order order) {
        orderProducer.sendOrderEvent("Order Placed: " + order.getId());
    }



}

