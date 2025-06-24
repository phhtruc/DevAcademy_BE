package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    NotificationService notificationService;

    @PostMapping("/send-reminder")
    public ResponseEntity<?> sendCourseReminder(@RequestParam String toEmail,
                                                @RequestParam Long courseId) {
        notificationService.sendCourseReminder(toEmail, courseId);
        return ResponseEntity.ok().build();
    }
}