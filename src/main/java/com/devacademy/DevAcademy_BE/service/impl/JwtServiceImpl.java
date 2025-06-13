package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    @Override
    public String generateToken(UserDetails userDetails, Boolean rememberMe) {
        return generateToken(new HashMap<>(), userDetails, rememberMe);
    }

    @Override
    public String extractUsername(String token, TokenType tokenType) {
        return extractClaims(token, Claims::getSubject, tokenType);
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String username = extractUsername(token, tokenType);
        return (userDetails.getUsername().equals(username) && !isTokenExpired(token, tokenType));
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails, Boolean rememberMe) {
        long refreshExpiration;
        if (rememberMe) {
            refreshExpiration = 2592000000L; // 30 days
        } else {
            refreshExpiration = 604800000L; // 7 days
        }
        return buildToken(new HashMap<>(), userDetails, refreshExpiration, TokenType.REFRESH_TOKEN);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails, Boolean rememberMe) {
        long jwtExpiration;
        if (rememberMe) {
            jwtExpiration = 604800000L; //7d
        } else {
            jwtExpiration = 900000L; //15p
        }
        return buildToken(claims, userDetails, jwtExpiration, TokenType.ACCESS_TOKEN);

    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration, TokenType tokenType) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType tokenType) {
        byte[] keyBytes = new byte[0];
        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } else if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            keyBytes = Decoders.BASE64.decode(refreshKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);

    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        final Claims claims = extractCAlllaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    private Claims extractCAlllaims(String token, TokenType tokenType) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date()); // kiểm tra xem ngày hết hạn có trước ngày hiên tại hay không
    }

    private Date extractExpiration(String token, TokenType tokenType) {
        return extractClaims(token, Claims::getExpiration, tokenType);
    } // lấy ra thời gian hết hạn của token

}
