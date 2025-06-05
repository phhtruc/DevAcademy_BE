package com.devacademy.DevAcademy_BE.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VNPayConfig {
    @Value("${vnpay.terminal-id}")
    private String vnpTerminalId;

    @Value("${vnpay.secret-key}")
    private String vnpHashSecret;

    @Value("${vnpay.payment-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;
}