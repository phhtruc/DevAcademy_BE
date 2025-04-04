package com.devacademy.DevAcademy_BE.exception;

import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}