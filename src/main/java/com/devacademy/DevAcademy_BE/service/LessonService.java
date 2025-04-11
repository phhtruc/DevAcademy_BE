package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonRequestDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LessonService {
    PageResponse<?> getLessonsByIdChapter(int page, int pageSize, Long idChapter);

    LessonResponseDTO getLessonById(Long id);

    LessonResponseDTO addLesson(LessonRequestDTO request);

    LessonResponseDTO updateLesson(Long id, LessonRequestDTO request);

    void deleteLesson(Long id);

    void updateOrder(List<OrderDTO> lessonOrderList);

    List<LessonResponseDTO> getAssignmentByIdChapter(Long id);

    List<LessonResponseDTO> getAllAssignment();

    PageResponse<?> getAllLesson(int page, int pageSize);
}
