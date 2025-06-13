package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.auth.AuthenticationRequest;
import com.devacademy.DevAcademy_BE.auth.AuthenticationResponse;
import com.devacademy.DevAcademy_BE.dto.ChangePasswordDTO;
import com.devacademy.DevAcademy_BE.dto.ResetPasswordDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(HttpServletRequest request);

    void resetPassword(ResetPasswordDTO resetPasswordDTO);

    void verifyToken(String token);

    void changePassword(ChangePasswordDTO changePasswordDTO, Authentication authentication);

    void forgotPassword(String email);
}
