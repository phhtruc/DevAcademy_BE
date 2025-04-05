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
public class UserRequestDTO {
    //@NotBlank(message = "FULL_NAME_INVALID")
    String fullName;
    //@NotBlank(message = "age not blank")
    String email;
}