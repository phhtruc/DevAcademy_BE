package com.devacademy.DevAcademy_BE.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendCourseReminder(String toEmail, Long courseId);
}
