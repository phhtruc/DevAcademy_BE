package com.devacademy.DevAcademy_BE.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.public-key}")
    private String publicKey;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = secretKey;
    }

    @Value("${vnpay.return-url}")
    private String returnUrl;
}
