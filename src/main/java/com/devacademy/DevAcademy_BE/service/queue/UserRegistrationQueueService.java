package com.devacademy.DevAcademy_BE.service.queue;

import com.devacademy.DevAcademy_BE.dto.queue.UserRegistrationTask;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.MailService;
import com.devacademy.DevAcademy_BE.service.cloudinary.CloudinaryService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserRegistrationQueueService {

    final BlockingQueue<UserRegistrationTask> registrationQueue = new LinkedBlockingQueue<>();
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    final UserRepository userRepository;
    final CloudinaryService cloudinaryService;
    final MailService mailService;

    @Value("${app.frontend.url}")
    String frontendUrl;

    @PostConstruct
    public void init() {
        executorService.submit(this::processQueue);
        log.info("Registration queue processor started");
    }

    public void addToQueue(UserRegistrationTask task) {
        try {
            registrationQueue.put(task);
            log.info("Task added to queue: {} for user ID: {}", task.getTaskType(), task.getUserId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to add task to queue", e);
        }
    }

    private void processQueue() {
        while (true) {
            try {
                UserRegistrationTask task = registrationQueue.take();
                log.info("Processing {} task for user ID: {}", task.getTaskType(), task.getUserId());

                switch (task.getTaskType()) {
                    case REGISTRATION:
                        processUserRegistration(task);
                        break;
                    case STATUS_CHANGE:
                        processStatusChange(task);
                        break;
                    case DELETION:
                        processUserDeletion(task);
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error processing task: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processUserRegistration(UserRegistrationTask task) {
        try {
            UserEntity user = userRepository.findById(task.getUserId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            if (task.hasFile()) {
                File tempFile = File.createTempFile("upload_", "_temp");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(task.getFileContent());
                }

                String avatarUrl = cloudinaryService.uploadImage(tempFile, task.getOriginalFilename(), task.getContentType());
                user.setAvatar(avatarUrl);
                userRepository.save(user);

                tempFile.delete();
            }

            // Send email
            String resetPasswordLink = String.format("%s/auth/create-password/token=%s", frontendUrl, task.getToken());
            String emailSubject = "Thiết lập mật khẩu tài khoản DevAcademy của bạn";
            mailService.setUpAccount(user.getFullName(), resetPasswordLink, user.getEmail(), emailSubject);
            log.info("Registration email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.info("Error in processUserRegistration: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void processStatusChange(UserRegistrationTask task) {
        try {
            UserEntity user = userRepository.findById(task.getUserId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            String subject = task.getIsActive() ?
                    "Tài khoản của bạn đã được kích hoạt" :
                    "Tài khoản của bạn đã bị vô hiệu hóa";

            mailService.sendAccountStatusChangeNotification(
                    user.getFullName(),
                    user.getEmail(),
                    task.getIsActive(),
                    subject
            );
            log.info("Status change email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.info("Error in processStatusChange: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void processUserDeletion(UserRegistrationTask task) {
        try {
            UserEntity user = userRepository.findById(task.getUserId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            String subject = "Tài khoản DevAcademy của bạn đã bị xóa";
            mailService.sendAccountDeletionNotification(
                    user.getFullName(),
                    user.getEmail(),
                    subject
            );
            log.info("Deletion email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.info("Error in processUserDeletion: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        System.out.println("Registration queue processor shutdown");
    }
}