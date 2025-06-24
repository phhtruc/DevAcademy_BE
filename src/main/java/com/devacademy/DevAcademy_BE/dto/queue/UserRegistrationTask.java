package com.devacademy.DevAcademy_BE.dto.queue;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegistrationTask {

    public enum TaskType {
        REGISTRATION,
        STATUS_CHANGE,
        DELETION
    }

    TaskType taskType;
    byte[] fileContent;
    String originalFilename;
    String contentType;
    UUID userId;
    String token;
    Boolean isActive;

    public UserRegistrationTask(TaskType taskType, byte[] fileContent, String originalFilename,
                                String contentType, UUID userId, String token, Boolean isActive) {
        this.taskType = taskType;
        this.fileContent = fileContent;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.userId = userId;
        this.token = token;
        this.isActive = isActive;
    }

    public static UserRegistrationTask registration(byte[] fileContent, String originalFilename,
                                                    String contentType, UUID userId, String token) {
        return new UserRegistrationTask(TaskType.REGISTRATION, fileContent, originalFilename,
                contentType, userId, token, null);
    }

    public static UserRegistrationTask statusChange(UUID userId, boolean isActive) {
        return new UserRegistrationTask(TaskType.STATUS_CHANGE, null, null,
                null, userId, null, isActive);
    }

    public static UserRegistrationTask deletion(UUID userId) {
        return new UserRegistrationTask(TaskType.DELETION, null, null,
                null, userId, null, null);
    }

    public boolean hasFile() {
        return fileContent != null;
    }
}

