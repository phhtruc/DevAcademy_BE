package com.devacademy.DevAcademy_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint client kết nối đến (ví dụ: "/ws")
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Client subscribe prefix (ví dụ: "/topic")
        registry.enableSimpleBroker("/topic");

        // Prefix cho các message gửi từ client (ví dụ: "/app")
        registry.setApplicationDestinationPrefixes("/app");
    }
}
