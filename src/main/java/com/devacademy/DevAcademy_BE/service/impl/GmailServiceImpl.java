package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GmailServiceImpl implements MailService {

    final JavaMailSender javaMailSender;
    final TemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    String frontendUrl;

    //static String SPRING_MAIL_USERNAME = System.getenv("SPRING_MAIL_USERNAME");
    static String SPRING_MAIL_USERNAME = "kientruc582@gmail.com";

    @Override
    public void setUpAccount(String userName, String resetLink, String toEmail, String subject) {
        Context context = createContextForSetupAccount(userName, resetLink);
        String htmlContent = templateEngine.process("emails/setup-account", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void forgotPassword(String resetLink, String toEmail, String subject){
        Context context = createContextForgotPassword(resetLink);
        String htmlContent = templateEngine.process("emails/forgot-password", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void buyCourseMail(String userName, Long courseId, String courseName, String toEmail, String subject) {
        Context context = createContext(userName, courseName, courseId);
        String htmlContent = templateEngine.process("emails/buy-course-user", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void durationCourse(String userName, String expiredDate, String courseName, String toEmail, String subject,
                               Long courseId) {
        Context context = createContextDuration(userName, courseName, expiredDate, courseId);
        String htmlContent = templateEngine.process("emails/duration-course", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(SPRING_MAIL_USERNAME, "DevAcademy"));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Context createContext(String userName, String courseName, Long courseId) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("courseName", courseName);
        context.setVariable("courseId", courseId);
        context.setVariable("frontendUrl", frontendUrl);
        return context;
    }

    private Context createContextForSetupAccount(String userName, String resetLink) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);
        return context;
    }

    private Context createContextForgotPassword(String resetLink) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("resetLink", resetLink);
        return context;
    }

    private Context createContextDuration(String userName, String courseName, String expiredDate, Long courseId) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("courseName", courseName);
        context.setVariable("expiredDate", expiredDate);
        context.setVariable("courseId", courseId);
        context.setVariable("frontendUrl", frontendUrl);
        return context;
    }
}
