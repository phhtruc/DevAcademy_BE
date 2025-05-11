package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.entity.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    RedisTemplate<String, Object> redisTemplate;

    public void saveToken(UserEntity user, String accessToken, int duration) {
        String key = buildKey(user.getId(), accessToken);

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("revoked", false);
        tokenData.put("expired", false);

        redisTemplate.opsForHash().putAll(key, tokenData);
        redisTemplate.expire(key, Duration.ofMinutes(duration));
    }

    public void revokeAllUserTokens(UUID userId) {
        Set<String> keys = redisTemplate.keys("token:" + userId + ":*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                redisTemplate.opsForHash().put(key, "revoked", true);
                redisTemplate.opsForHash().put(key, "expired", true);
            }
        }
    }

    public boolean isTokenValid(UUID userId, String token) {
        String key = buildKey(userId, token);
        Boolean revoked = (Boolean) redisTemplate.opsForHash().get(key, "revoked");
        Boolean expired = (Boolean) redisTemplate.opsForHash().get(key, "expired");

        return Boolean.FALSE.equals(revoked) && Boolean.FALSE.equals(expired);
    }

    private String buildKey(UUID userId, String token) {
        return "token:" + userId + ":" + token;
    }

    public void deleteToken(UUID userId, String token) {
        String key = buildKey(userId, token);
        redisTemplate.delete(key);
    }
}
