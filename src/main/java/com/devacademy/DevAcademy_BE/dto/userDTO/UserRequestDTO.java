package com.devacademy.DevAcademy_BE.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDTO {
    @NotBlank(message = "FULL_NAME_INVALID")
    String fullName;
    @NotBlank(message = "EMAIL_INVALID")
    String email;
    @NotBlank(message = "PASSWORD_INVALID")
    String password;
    @Pattern(regexp = "ADMIN|TEACHER|USER", message = "ROLE_INVALID_TYPE")
    String roles;
}