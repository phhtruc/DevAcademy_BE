package com.devacademy.DevAcademy_BE.dto.userDTO;

import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDTO implements Serializable {
    UUID id;
    String fullName;
    String avatar;
    UserStatus status;
    String roles;
    String email;
    LocalDateTime createdDate;
    LocalDateTime modifiedDate;
}
