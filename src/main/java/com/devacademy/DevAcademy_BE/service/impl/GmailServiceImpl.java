package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GmailServiceImpl implements MailService {

    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    static String SPRING_MAIL_USERNAME = System.getenv("SPRING_MAIL_USERNAME");

    @Override
    public void trialCourseMail(String userName, String courseName, String toEmail, String subject, String title, String description) throws MessagingException {
        Context context = createContext(userName, courseName);
        String htmlContent = templateEngine.process("email/trial-course", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void setUpAccount(String userName, String resetLink, String toEmail, String subject) throws MessagingException {
        Context context = createContextForSetupAccount(userName, resetLink);
        String htmlContent = templateEngine.process("email/setup-account", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void buyCourseMail(String userName, String courseName, String toEmail, String subject, String title, String description) throws MessagingException {
        Context context = createContext(userName, courseName);
        String htmlContent = templateEngine.process("email/buy-course-user", context);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(SPRING_MAIL_USERNAME);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    private Context createContext(String userName, String courseName) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("courseName", courseName);
        return context;
    }

    private Context createContextForSetupAccount(String userName, String resetLink) {
        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);
        return context;
    }


}
