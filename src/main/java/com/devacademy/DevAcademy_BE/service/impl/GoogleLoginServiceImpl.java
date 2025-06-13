package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.auth.AuthenticationResponse;
import com.devacademy.DevAcademy_BE.entity.RoleEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.JwtService;
import com.devacademy.DevAcademy_BE.service.SocialService;
import com.devacademy.DevAcademy_BE.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class GoogleLoginServiceImpl implements SocialService {

    static String GOOGLE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    static String CLIENT_ID = System.getenv("CLIENT_ID");
    static String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    static String REDIRECT_URI = System.getenv("REDIRECT_URI");
    static String TOKEN_URL = "https://oauth2.googleapis.com/token";
    static String USERINFO = "https://www.googleapis.com/oauth2/v2/userinfo";
    UserRepository userRepository;
    JwtService jwtService;
    TokenService redisTokenService;
    UserServiceImpl userServiceImpl;
    UserDetailsService userDetailsService;

    @Override
    public String getSocialLoginUrl() {
        String scope = "openid%20email%20profile";
        return GOOGLE_URL
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code"
                + "&scope=" + scope;
    }

    @Override
    public AuthenticationResponse handleSocialLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> request = buildTokenRequest(code);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                TOKEN_URL, HttpMethod.POST, request,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> tokenBody = response.getBody();
        if (tokenBody == null || !tokenBody.containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token from Google");
        }

        String accessToken = (String) tokenBody.get("access_token");

        Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);
        String email = (String) userInfo.get("email");
        String fullName = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .email(email)
                            .fullName(fullName)
                            .avatar(picture)
                            .isDeleted(false)
                            .status(UserStatus.ACTIVE)
                            .build();
                    UserEntity savedUser = userRepository.save(newUser);

                    RoleEntity resolvedRole = userServiceImpl.resolveRole(null);
                    userServiceImpl.associateRoleWithUser(savedUser, resolvedRole);

                    return userRepository.findById(savedUser.getId()).orElseThrow();
                });

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        UserEntity refreshedUser = (UserEntity) userDetails;

        String jwtToken = jwtService.generateToken(refreshedUser, false);
        String refreshToken = jwtService.generateRefreshToken(refreshedUser, false);

        return buildAuthenticationResponse(refreshedUser, jwtToken, refreshToken);

    }

    private HttpEntity<MultiValueMap<String, String>> buildTokenRequest(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(params, headers);
    }

    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                USERINFO, HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Failed to get user info from Google");
        }

        return body;
    }

    private AuthenticationResponse buildAuthenticationResponse(UserEntity user, String accessToken, String refreshToken) {
        redisTokenService.revokeAllUserTokens(user.getId());
        redisTokenService.saveToken(user, accessToken, 30);
        redisTokenService.saveToken(user, refreshToken, 11111);

        return AuthenticationResponse.builder()
                .tokenType(TokenType.BEARER)
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getAuthorities().toString())
                .message("Login success")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
