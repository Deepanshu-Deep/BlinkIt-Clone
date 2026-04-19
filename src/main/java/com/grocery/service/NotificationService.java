package com.grocery.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    void sendOrderConfirmation(String email);
}