package com.devacademy.DevAcademy_BE.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Setter
public class ChangePasswordDTO {
    @NotBlank(message = "OLD_PASSWORD_INVALID")
    private String oldPassword;

    @NotBlank(message = "NEW_PASSWORD_INVALID")
    private String newPassword;
}