package com.devacademy.DevAcademy_BE.service.socket;

import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    // Broadcast video upload progress to WebSocket topic
    public void broadcastProgress(VideoStatusResponse videoStatus) {
        try {
            messagingTemplate.convertAndSend("/topic/progress", videoStatus);
            log.info("Broadcast progress thành công cho lesson {}: {}%",
                    videoStatus.getLessonId(), videoStatus.getProgress());
        } catch (Exception e) {
            log.error("Lỗi khi gửi progress qua WebSocket: {}", e.getMessage());
        }
    }
}