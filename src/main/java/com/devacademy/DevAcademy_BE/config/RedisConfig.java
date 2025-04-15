package com.devacademy.DevAcademy_BE.config;

import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
    }

    @Bean
    public RedisTemplate<String, VideoStatusResponse> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, VideoStatusResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Serializer for value
        Jackson2JsonRedisSerializer<VideoStatusResponse> serializer =
                new Jackson2JsonRedisSerializer<>(VideoStatusResponse.class);

        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}

