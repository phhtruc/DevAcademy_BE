package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.CourseRepository;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.MailService;
import com.devacademy.DevAcademy_BE.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

    MailService mailService;
    UserRepository userRepository;
    CourseRepository courseRepository;

    @Override
    public void sendCourseReminder(String toEmail, Long courseId) {

        UserEntity user = userRepository.findByEmail(toEmail)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));

        String subject = "[Dev Academy] Nhắc nhở tiếp tục học tập";
        try {
            mailService.sendCourseReminder(user.getFullName(), course.getName(), toEmail, subject, courseId);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.EMAIL_SEND_FAILED);
        }

    }
}
