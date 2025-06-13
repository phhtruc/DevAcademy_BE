package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.auth.AuthenticationRequest;
import com.devacademy.DevAcademy_BE.auth.AuthenticationResponse;
import com.devacademy.DevAcademy_BE.dto.ChangePasswordDTO;
import com.devacademy.DevAcademy_BE.dto.ResetPasswordDTO;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.AuthenticationService;
import com.devacademy.DevAcademy_BE.service.JwtService;
import com.devacademy.DevAcademy_BE.service.MailService;
import com.devacademy.DevAcademy_BE.service.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {

    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final JwtService jwtService;
    final TokenService redisTokenService;
    final UserDetailsService userDetailsService;
    final PasswordEncoder passwordEncoder;
    final MailService mailService;

    @Value("${app.frontend.url}")
    String frontendUrl;

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

        String accessToken;
        String refreshToken;
        if (request.getRememberMe()) {
            accessToken = jwtService.generateToken(user, true);
            refreshToken = jwtService.generateRefreshToken(user, true);
        } else {
            accessToken = jwtService.generateToken(user, false);
            refreshToken = jwtService.generateRefreshToken(user, false);
        }
        return buildAuthenticationResponse(user, accessToken, refreshToken,
                "Login success", request.getRememberMe());
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
        String accessToken = jwtService.generateToken(user.get(), false);

        return buildAuthenticationResponse(user.get(), accessToken, refreshToken,
                "Refresh token success", false);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        UUID userId = getUserId(resetPasswordDTO.getToken());

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        redisTokenService.deleteToken(userId, resetPasswordDTO.getToken());
    }

    @Override
    public void verifyToken(String token) {
        UUID userId = getUserId(token);
        boolean isValid = redisTokenService.isTokenValid(userId, token);

        if (!isValid) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
        redisTokenService.revokeAllUserTokens(user.getId());
    }

    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        String token = jwtService.generateToken(user, false);
        redisTokenService.saveToken(user, token, 1440);

        String resetPasswordLink = String.format("%s/auth/create-password/token=%s", frontendUrl, token);
        String emailSubject = "[Reset Password] Quen Mat Khau";
        mailService.forgotPassword(resetPasswordLink, email, emailSubject);
    }

    private AuthenticationResponse buildAuthenticationResponse(UserEntity user, String accessToken, String refreshToken,
                                                               String message, Boolean rememberMe) {
        redisTokenService.revokeAllUserTokens(user.getId());
        if (rememberMe) {
            redisTokenService.saveToken(user, accessToken, 11520); // 8 days
            redisTokenService.saveToken(user, refreshToken, 44640); // 31 days
        }
        redisTokenService.saveToken(user, accessToken, 30);
        redisTokenService.saveToken(user, refreshToken, 11520);

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

    private UUID getUserId(String token) {
        String username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return ((UserEntity) userDetails).getId();
    }
}
