package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.auth.AuthenticationRequest;
import com.devacademy.DevAcademy_BE.auth.AuthenticationResponse;
import com.devacademy.DevAcademy_BE.entity.TokenEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.AuthenticationService;
import com.devacademy.DevAcademy_BE.service.JwtService;
import com.devacademy.DevAcademy_BE.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    JwtService jwtService;
    TokenService tokenService;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email or Password is incorrect"));
        String JwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthenticationResponse(user, JwtToken, refreshToken, "Login success");
    }

    @Override
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("x-token");
        if (StringUtils.isBlank(refreshToken)) {
            throw new RuntimeException("token must be not blank");
        }
        final String email = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (!jwtService.isTokenValid(refreshToken, user.get(), TokenType.REFRESH_TOKEN)) {
            throw new RuntimeException("token is invalid");
        }
        String accessToken = jwtService.generateToken(user.get());

        return buildAuthenticationResponse(user.get(), accessToken, refreshToken, "Refresh token success");
    }

    private AuthenticationResponse buildAuthenticationResponse(UserEntity user, String accessToken, String refreshToken,
                                                               String message) {
        tokenService.revokeAllUserTokens(user);

        tokenService.saveToken(TokenEntity.builder()
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .userEntity(user)
                .expired(false)
                .revoked(false)
                .isDeleted(false)
                .build());

        return AuthenticationResponse.builder()
                .tokenType(TokenType.BEARER)
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getAuthorities().toString())
                .message(message)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
