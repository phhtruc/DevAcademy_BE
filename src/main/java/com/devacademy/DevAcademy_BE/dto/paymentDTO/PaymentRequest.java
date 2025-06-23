package com.devacademy.DevAcademy_BE.dto.paymentDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    Long courseId;
    UUID userId;
    String bankCode;
    Long amount;
    String email;
    String courseName;
    String language;
    String paymentMethod;
}
