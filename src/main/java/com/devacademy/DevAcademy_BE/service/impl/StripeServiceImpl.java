package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.config.StripeConfig;
import com.devacademy.DevAcademy_BE.dto.paymentDTO.PaymentRequest;
import com.devacademy.DevAcademy_BE.dto.paymentDTO.PaymentResponse;
import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.CoursePayEntity;
import com.devacademy.DevAcademy_BE.entity.CourseRegisterEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.PayStatus;
import com.devacademy.DevAcademy_BE.enums.PayType;
import com.devacademy.DevAcademy_BE.enums.RegisterStatus;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.devacademy.DevAcademy_BE.repository.CoursePayRepository;
import com.devacademy.DevAcademy_BE.repository.CourseRegisterRepository;
import com.devacademy.DevAcademy_BE.service.MailService;
import com.devacademy.DevAcademy_BE.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service("stripe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripeServiceImpl implements PaymentService {

    CoursePayRepository coursePayRepository;
    CourseRegisterRepository courseRegisterRepository;
    MailService mailService;
    StripeConfig stripeConfig;

    @Override
    public PaymentResponse createPaymentUrl(PaymentRequest paymentRequest, HttpServletRequest request) {
        String txnRef = generateTxnRef(paymentRequest.getCourseId());
        String courseName = URLEncoder.encode(paymentRequest.getCourseName(), StandardCharsets.UTF_8);
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(stripeConfig.getReturnUrl() + "?courseName=" + courseName
                            + "&courseId=" + paymentRequest.getCourseId() + "&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(stripeConfig.getReturnUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("vnd")
                                    .setUnitAmount(paymentRequest.getAmount())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Khóa học: " + paymentRequest.getCourseName())
                                                    .build())
                                    .build())
                            .setQuantity(1L)
                            .build())
                    .setClientReferenceId(txnRef)
                    .build();

            Session session = Session.create(params);

            return PaymentResponse.builder()
                    .paymentUrl(session.getUrl())
                    .txnRef(txnRef)
                    .amount(paymentRequest.getAmount())
                    .sessionId(session.getId())
                    .build();

        } catch (StripeException e) {
            log.error("Error creating Stripe session", e);
            throw new RuntimeException("Failed to create Stripe payment session", e);
        }
    }

    @Override
    public Map<String, Object> processPaymentReturn(HttpServletRequest request,
                                                    Authentication authentication,
                                                    String courseName,
                                                    Long courseId) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String sessionId = request.getParameter("session_id");
        Map<String, Object> result = new HashMap<>();

        try {
            Session session = Session.retrieve(sessionId);

            if ("complete".equals(session.getStatus())) {
                result.put("success", true);
                result.put("message", "Thanh toán thành công");

                savePaymentRecord(user, courseId, true, session);
                createCourseRegistration(user, courseId);

                try {
                    mailService.buyCourseMail(
                            user.getFullName(),
                            courseId,
                            courseName,
                            user.getEmail(),
                            "Mua khóa học thành công"
                    );
                } catch (Exception e) {
                    log.error("Error sending email confirmation", e);
                }
            } else {
                result.put("success", false);
                result.put("message", "Thanh toán thất bại");
                result.put("responseCode", session.getStatus());
                savePaymentRecord(user, courseId, false, session);
            }

            return result;

        } catch (StripeException e) {
            log.error("Error processing Stripe payment return", e);
            result.put("success", false);
            result.put("message", "Lỗi xử lý thanh toán");
            return result;
        }
    }

    private String generateTxnRef(Long courseId) {
        return String.valueOf(System.currentTimeMillis() + courseId);
    }

    private void savePaymentRecord(UserEntity user, Long courseId, boolean isSuccessful, Session session) {
        try {
            CoursePayEntity paymentRecord = CoursePayEntity.builder()
                    .userEntity(user)
                    .courseEntity(CourseEntity.builder().id(courseId).build())
                    .price(new BigDecimal(session.getAmountTotal()))
                    .method(PayType.STRIPE)
                    .status(isSuccessful ? PayStatus.SUCCESSFUL : PayStatus.FAILED)
                    .isDeleted(false)
                    .build();

            coursePayRepository.save(paymentRecord);
        } catch (Exception e) {
            log.error("Error saving payment record", e);
        }
    }

    private void createCourseRegistration(UserEntity user, Long courseId) {
        try {
            CourseRegisterEntity registration = CourseRegisterEntity.builder()
                    .userEntity(user)
                    .courseEntity(CourseEntity.builder().id(courseId).build())
                    .registerType(RegisterType.BUY)
                    .status(RegisterStatus.CONFIRMED)
                    .isDeleted(false)
                    .build();

            courseRegisterRepository.save(registration);
        } catch (Exception e) {
            log.error("Error creating course registration", e);
        }
    }
}