package com.grocery.serviceImpl;

import com.grocery.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    @Async
    public void sendOrderConfirmation(String email) {

        System.out.println("Sending order confirmation email to: " + email);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Email sent successfully to: " + email);
    }
}