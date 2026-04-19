package com.grocery.serviceImpl;

import com.grocery.model.Order;
import com.grocery.repository.OrderRepository;
import com.grocery.repository.UserRepository;
import com.grocery.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // Cache user count
    @Override
    @Cacheable(value = "admin-dashboard", key = "'userCount'")
    public long getUserCount() {

        return userRepository.count();
    }

    // Cache order count
    @Override
    @Cacheable(value = "admin-dashboard", key = "'orderCount'")
    public long getOrderCount() {

        return orderRepository.count();
    }

    // Cache total revenue
    @Override
    @Cacheable(value = "admin-dashboard", key = "'totalRevenue'")
    public double getTotalRevenue() {

        return orderRepository.findAll()
                .stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }
}