package com.devacademy.DevAcademy_BE.dto.websocket;

import com.devacademy.DevAcademy_BE.enums.SocketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {
    private SocketType type;
    private Long lessonId;
    private T payload;
}