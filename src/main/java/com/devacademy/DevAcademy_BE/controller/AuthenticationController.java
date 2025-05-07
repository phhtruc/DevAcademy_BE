package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.auth.AuthenticationRequest;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.service.AuthenticationService;
import com.devacademy.DevAcademy_BE.service.UserService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
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
    public ResponseEntity<?> register(@ModelAttribute @Valid UserRequestDTO request,
                                      @RequestParam(required = false) MultipartFile file) throws IOException {
        return JsonResponse.ok(userService.addUser(request, file));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request){
        return JsonResponse.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return JsonResponse.ok(authenticationService.refreshToken(request));
    }
}
