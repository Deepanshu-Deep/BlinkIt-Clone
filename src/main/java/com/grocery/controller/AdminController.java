package com.grocery.controller;

import com.grocery.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // ADMIN DASHBOARD
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {

        logger.info("Fetching admin dashboard");

        Map<String, Object> response = new HashMap<>();

        response.put("totalUsers", adminService.getUserCount());
        response.put("totalOrders", adminService.getOrderCount());
        response.put("totalRevenue", adminService.getTotalRevenue());

        return ResponseEntity.ok(response);
    }
}