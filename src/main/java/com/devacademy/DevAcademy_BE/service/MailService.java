package com.devacademy.DevAcademy_BE.service;

import jakarta.mail.MessagingException;

import java.util.List;

public interface MailService {

    void trialCourseMail(String userName, String courseName , String toEmail, String subject, String title, String description) throws MessagingException;

    void buyCourseMail(String studentName, List<String> teacherName, String courseName, List<String> teacherEmailAddress, String userEmailAddress) throws MessagingException;

    void sendSimpleEmail(String toEmail, String subject, String body);

    void setUpAccount(String userName, String resetLink, String toEmail, String subject) throws MessagingException;
}