package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.config.VNPayConfig;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("vnpay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VNPayServiceImpl implements PaymentService {

    VNPayConfig vnPayConfig;
    CoursePayRepository coursePayRepository;
    CourseRegisterRepository courseRegisterRepository;
    MailService mailService;

    @Override
    public PaymentResponse createPaymentUrl(PaymentRequest paymentRequest, HttpServletRequest request) {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String orderType = "other";
        String clientIp = getClientIp(request);
        String txnRef = generateTxnRef(paymentRequest.getCourseId());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnPayConfig.getVnpTerminalId());
        vnpParams.put("vnp_Amount", String.valueOf(paymentRequest.getAmount() * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toán khóa học: " + paymentRequest.getCourseName());
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", paymentRequest.getLanguage() == null ? "vn" : paymentRequest.getLanguage());
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnpParams.put("vnp_IpAddr", clientIp);

        if (paymentRequest.getBankCode() != null && !paymentRequest.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", paymentRequest.getBankCode());
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Sort and encode
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                    hashData.append(fieldName).append("=").append(encodedValue);
                    query.append(fieldName).append("=").append(encodedValue);

                    if (i < fieldNames.size() - 1) {
                        hashData.append("&");
                        query.append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("Encoding error", e);
                    throw new RuntimeException("Error encoding VNPAY parameters", e);
                }
            }
        }

        String vnpSecureHash = hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);

        String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + query;

        return PaymentResponse.builder()
                .paymentUrl(paymentUrl)
                .txnRef(txnRef)
                .amount(paymentRequest.getAmount())
                .build();
    }

    public boolean validateReturnData(Map<String, String> vnpParams) {
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");

        Map<String, String> validParams = new HashMap<>(vnpParams);
        validParams.remove("vnp_SecureHash");
        validParams.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(validParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = validParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                    hashData.append(fieldName).append("=").append(encodedValue);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnpSecureHash);
    }

    @Override
    public Map<String, Object> processPaymentReturn(HttpServletRequest request,
                                                    Authentication authentication,
                                                    String courseName,
                                                    Long courseId) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        Map<String, String> vnpParams = new HashMap<>();
        Map<String, String[]> fields = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : fields.entrySet()) {
            String key = entry.getKey();
            if (!"courseId".equals(key) && !"courseName".equals(key)) {
                vnpParams.put(key, entry.getValue()[0]);
            }
        }

        Map<String, Object> result = new HashMap<>();

        if (!validateReturnData(vnpParams)) {
            result.put("success", false);
            result.put("message", "Chữ ký không hợp lệ");
            savePaymentRecord(user, courseId, false, vnpParams);
            return result;
        }

        String responseCode = vnpParams.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            result.put("success", true);
            result.put("message", "Thanh toán thành công");

            savePaymentRecord(user, courseId, true, vnpParams);
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
            result.put("responseCode", responseCode);
            savePaymentRecord(user, courseId, false, vnpParams);
        }

        return result;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(keySpec);
            byte[] hmacBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            log.error("HMAC SHA512 error", e);
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        return (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : request.getRemoteAddr();
    }

    private String generateTxnRef(Long courseId) {
        return String.valueOf(System.currentTimeMillis() + courseId);
    }

    private void savePaymentRecord(UserEntity user, Long courseId, boolean isSuccessful, Map<String, String> vnpParams) {
        try {
            CoursePayEntity paymentRecord = CoursePayEntity.builder()
                    .userEntity(user)
                    .courseEntity(CourseEntity.builder().id(courseId).build())
                    .price(new BigDecimal(Long.parseLong(vnpParams.get("vnp_Amount")) / 100))
                    .method(PayType.VNPAY)
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
