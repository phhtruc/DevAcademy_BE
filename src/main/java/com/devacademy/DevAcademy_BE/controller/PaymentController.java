package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.paymentDTO.PaymentRequest;
import com.devacademy.DevAcademy_BE.service.PaymentService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    ApplicationContext applicationContext;

    @PostMapping("/create-payment-url")
    public ResponseEntity<?> createPaymentUrl(@RequestBody PaymentRequest paymentRequest,
                                              HttpServletRequest request) {
        PaymentService paymentService = (PaymentService) applicationContext
                .getBean(paymentRequest.getPaymentMethod());
        return JsonResponse.ok(paymentService.createPaymentUrl(paymentRequest, request));
    }

    @GetMapping("/payment-return")
    public ResponseEntity<?> paymentReturn(HttpServletRequest request,
                                           Authentication authentication,
                                           @RequestParam String courseName,
                                           @RequestParam Long courseId,
                                           @RequestParam String provider) {
        PaymentService paymentService = (PaymentService) applicationContext.getBean(provider);

        return JsonResponse.ok(paymentService.processPaymentReturn(request, authentication, courseName, courseId));
    }

    @GetMapping("/payment-return/stripe")
    public ResponseEntity<?> stripeReturn(HttpServletRequest request,
                                          Authentication authentication,
                                          @RequestParam String courseName,
                                          @RequestParam Long courseId,
                                          @RequestParam String provider) {
        PaymentService paymentService = (PaymentService) applicationContext.getBean(provider);

        return JsonResponse.ok(paymentService.processPaymentReturn(request, authentication, courseName, courseId));
    }
}
