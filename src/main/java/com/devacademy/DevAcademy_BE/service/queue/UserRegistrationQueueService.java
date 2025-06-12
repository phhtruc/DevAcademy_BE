package com.devacademy.DevAcademy_BE.service.queue;

import com.devacademy.DevAcademy_BE.dto.UserRegistrationTask;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    }

    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void addToQueue(UserRegistrationTask task) {
        registrationQueue.add(task);
    }

    private void processQueue() {
        while (true) {
            try {
                UserRegistrationTask task = registrationQueue.take();
                processUserRegistration(task);
            } catch (Exception e) {
                // Log exception but continue processing
                e.printStackTrace();
            }
        }
    }

    private void processUserRegistration(UserRegistrationTask task) {
        try {
            UserEntity user = userRepository.findById(task.getUserId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            if (task.getFile() != null) {
                String avatarUrl = cloudinaryService.uploadImage(task.getFile());
                user.setAvatar(avatarUrl);
                userRepository.save(user);
            }

            // Send email
            String resetPasswordLink = String.format("%s/auth/create-password/token=%s", frontendUrl, task.getToken());
            String emailSubject = "Set up your DevAcademy account password";
            mailService.setUpAccount(user.getFullName(), resetPasswordLink, user.getEmail(), emailSubject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}