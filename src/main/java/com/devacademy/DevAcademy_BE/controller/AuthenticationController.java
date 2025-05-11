package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.auth.AuthenticationRequest;
import com.devacademy.DevAcademy_BE.dto.ResetPasswordDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.service.AuthenticationService;
import com.devacademy.DevAcademy_BE.service.UserService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequestDTO request){
        return JsonResponse.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request){
        return JsonResponse.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return JsonResponse.ok(authenticationService.refreshToken(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        authenticationService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Password reset successfully!");
    }

    @GetMapping("/verify-token/{token}")
    public ResponseEntity<String> verifyToken(@PathVariable String token) {
        authenticationService.verifyToken(token);
        return ResponseEntity.ok().build();
    }
}
