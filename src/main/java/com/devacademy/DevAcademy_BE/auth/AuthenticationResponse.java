package com.devacademy.DevAcademy_BE.auth;

import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    @Enumerated(EnumType.STRING)
    TokenType tokenType;
    UUID id;
    String email;
    String roles;
    String message;
    @JsonProperty("accessToken")
    String accessToken;
    @JsonProperty("refreshToken")
    String refreshToken;
}
