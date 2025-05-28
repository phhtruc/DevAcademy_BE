package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.paymentDTO.PaymentRequest;
import com.devacademy.DevAcademy_BE.dto.paymentDTO.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface VNPayService {
    PaymentResponse createPaymentUrl(PaymentRequest paymentRequest, HttpServletRequest request);
    Map<String, Object> processPaymentReturn(HttpServletRequest request);
}
