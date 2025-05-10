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

import java.util.List;
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
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context(Locale.getDefault());
        context.setVariable("userName", userName);
        context.setVariable("courseName", courseName);

        String htmlContent = templateEngine.process("email/trial-course", context);

        try {
            helper.setFrom(SPRING_MAIL_USERNAME);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        }catch (MessagingException e){
            throw new RuntimeException("Error sending email", e);
        }

    }

    @Override
    public void buyCourseMail(String studentName, List<String> teacherName, String courseName, List<String> teacherEmailAddress, String userEmailAddress) throws MessagingException {
        MimeMessage studentMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(studentMessage, true, "UTF-8");
        String htmlBody = buildEmailUserBuyCourse(studentName, courseName);

        helper.setFrom(SPRING_MAIL_USERNAME);
        helper.setTo(userEmailAddress);
        helper.setSubject("Thông báo mua khóa học thành công");
        helper.setText(htmlBody, true);

        javaMailSender.send(studentMessage);
        for (int i = 0; i < teacherName.size(); i++) {
            htmlBody = buildEmailTeacherBuyCourse(teacherName.get(i), studentName, courseName);

            helper.setFrom(SPRING_MAIL_USERNAME);
            helper.setTo(teacherEmailAddress.get(i));
            helper.setSubject("Thông báo có học viên mới đăng kí khóa học");
            helper.setText(htmlBody, true);

            javaMailSender.send(studentMessage);
        }
    }

    private String buildEmailUserBuyCourse(String userName, String courseName) {
        return
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Email Confirm</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        "        .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); border: 0.5px solid black }" +
                        "        .header { background-color: #28a745; padding: 10px; text-align: center; color: #ffffff; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                        "        .content { padding: 20px; text-align: left; color: #333333; }" +
                        "        .content h2 { color: #28a745; }" +
                        "        .content p { line-height: 1.6; }" +
                        "        .button { display: inline-block; padding: 10px 20px; background-color: #28a745; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1>Mua khóa học thành công</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <h2>Xin chào! " + userName + ",</h2>" +
                        "            <p>Chúc mừng! Bạn đã mua thành công khóa học <strong>" + courseName + "</strong>. Chúng tôi rất vui mừng đồng hành cùng bạn trong suốt hành trình học tập này.</p>" +
                        "            <p>Hãy kiểm tra lại thông tin khóa học và thời gian bắt đầu để đảm bảo bạn không bỏ lỡ bất kỳ điều gì nhé.</p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Cảm ơn bạn đã tin tưởng và lựa chọn nền tảng của chúng tôi.</p>" +
                        "            <p>TechLearn, 6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }


    private String buildEmailTeacherBuyCourse(String teacherName, String studentName, String courseName) {
        return
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "    <title>Email Confirm</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                        "        .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); border: 0.5px solid black }" +
                        "        .header { background-color: #ffc107; padding: 10px; text-align: center; color: #ffffff; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                        "        .content { padding: 20px; text-align: left; color: #333333; }" +
                        "        .content h2 { color: #ffc107; }" +
                        "        .content p { line-height: 1.6; }" +
                        "        .button { display: inline-block; padding: 10px 20px; background-color: #ffc107; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                        "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h1>Thông báo học viên tham gia khóa học</h1>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <h2>Xin chào giáo viên " + teacherName + ",</h2>" +
                        "            <p>Khóa học <strong>" + courseName + "</strong> vừa được mua thành công bởi một học viên <strong> " + studentName + "</strong> </p>" +
                        "            <p>Hãy chuẩn bị giáo án và sẵn sàng cho buổi học sắp tới. Chúng tôi luôn sẵn sàng hỗ trợ bạn trong quá trình giảng dạy.</p>" +
                        "        </div>" +
                        "        <div class=\"footer\">" +
                        "            <p>Cảm ơn bạn đã đồng hành cùng nền tảng của chúng tôi!</p>" +
                        "            <p>TechLearn, 6 Trần Phú, Thạch Thang, Hải Châu, Đà Nẵng</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }

}
