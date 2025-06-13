package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.auth.AuthenticationResponse;
import org.springframework.stereotype.Service;

@Service
public interface SocialService {
    String getSocialLoginUrl();
    AuthenticationResponse handleSocialLogin(String code);
}
