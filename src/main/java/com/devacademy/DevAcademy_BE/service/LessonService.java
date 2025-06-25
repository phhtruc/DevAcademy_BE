package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonRequestDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonResponseDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonSearchDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface LessonService {
    PageResponse<?> getLessonsByIdChapter(int page, int pageSize, Long idChapter, Long idCourse, Authentication auth);

    LessonResponseDTO getLessonById(Long id, Authentication auth);

    LessonResponseDTO addLesson(LessonRequestDTO request, MultipartFile video) throws IOException;

    LessonResponseDTO updateLesson(Long id, LessonRequestDTO request, MultipartFile video) throws IOException;

    void deleteLesson(Long id);

    void updateOrder(List<OrderDTO> lessonOrderList);

    List<LessonResponseDTO> getAssignmentByIdChapter(Long id);

    List<LessonResponseDTO> getAllAssignment();

    VideoStatusResponse getVideoUploadStatus(Long id);

    PageResponse<?> searchLessons(LessonSearchDTO searchDTO, int page, int pageSize);

    void updateLessonProgress(Long lessonId, Authentication authentication);
}
