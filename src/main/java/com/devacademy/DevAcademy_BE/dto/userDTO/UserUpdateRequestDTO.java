package com.devacademy.DevAcademy_BE.dto.userDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequestDTO {
    String fullName;
    String email;
    String roles;
    String status;
}