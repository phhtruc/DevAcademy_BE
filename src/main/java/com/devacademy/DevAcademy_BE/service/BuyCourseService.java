package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.BuyCourseResponseDTO;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface BuyCourseService {
    BuyCourseResponseDTO buyCourse(UUID id, long id_course) throws MessagingException;
}
