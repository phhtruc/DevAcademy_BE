package com.devacademy.DevAcademy_BE.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = secretKey;
    }
}
