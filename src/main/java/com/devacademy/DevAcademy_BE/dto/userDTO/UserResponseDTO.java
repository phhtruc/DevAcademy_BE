package com.devacademy.DevAcademy_BE.dto.userDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO implements Serializable {
    UUID id;
    String hoTen;
    String email;
}
