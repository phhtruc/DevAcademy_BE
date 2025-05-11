package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.UserRegistrationTask;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegistrationQueueService {

    BlockingQueue<UserRegistrationTask> registrationQueue = new LinkedBlockingQueue<>();
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    UserRepository userRepository;
    CloudinaryService cloudinaryService;
    MailService mailService;

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
            String resetPasswordLink = String.format("http://localhost:5173/auth/create-password/token=%s", task.getToken());
            String emailSubject = "Set up your DevAcademy account password";
            mailService.setUpAccount(user.getFullName(), resetPasswordLink, user.getEmail(), emailSubject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}