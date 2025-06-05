package com.devacademy.DevAcademy_BE.auth;

import com.devacademy.DevAcademy_BE.enums.Platform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "PASSWORD_INVALID")
    String password;

    @NotNull(message = "Platform must be not null")
    Platform platform;

    // Danh cho Mobile
    String deviceToken;

    String version;
}
