package com.devacademy.DevAcademy_BE.service;

import jakarta.mail.MessagingException;

public interface MailService {

    void buyCourseMail(String userName, Long courseId, String courseName, String toEmail, String subject) throws MessagingException;

    void durationCourse(String userName, String expiredDate, String courseName, String toEmail, String subject,
                        Long courseId) throws MessagingException;

    void setUpAccount(String userName, String resetLink, String toEmail, String subject) throws MessagingException;

    void forgotPassword(String resetLink, String toEmail, String subject);

    void sendCourseReminder(String userName, String courseName, String toEmail, String subject, Long courseId);
}