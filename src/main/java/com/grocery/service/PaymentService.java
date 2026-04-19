package com.grocery.service;

import com.grocery.dto.PaymentDTO;

import java.util.List;

@Deprecated
public interface PaymentService {

    PaymentDTO createPayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getAllPayments();
    PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO);
    void deletePayment(Long id);


}

