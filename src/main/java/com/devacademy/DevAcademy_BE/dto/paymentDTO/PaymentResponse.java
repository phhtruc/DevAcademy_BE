package com.devacademy.DevAcademy_BE.dto.paymentDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String paymentUrl;
    String txnRef;
    Long amount;
    String sessionId;
}
