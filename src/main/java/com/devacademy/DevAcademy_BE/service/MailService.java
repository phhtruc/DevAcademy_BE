package com.devacademy.DevAcademy_BE.service;

import jakarta.mail.MessagingException;

import java.util.List;

public interface MailService {

    void trialCourseMail(String userName, String courseName , String toEmail, String subject, String title, String description) throws MessagingException;

    void buyCourseMail(String userName, String courseName, String toEmail, String subject, String title, String description) throws MessagingException;

    void setUpAccount(String userName, String resetLink, String toEmail, String subject) throws MessagingException;
}