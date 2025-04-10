package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CourseService {
    PageResponse<?> getAllCourse(int page, int size);

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO addCourse(CourseRequestDTO request, MultipartFile file) throws IOException;

    CourseResponseDTO updateCourse(Long id, CourseRequestDTO request, MultipartFile file);

    void deleteCourse(Long id);

    List<CourseResponseDTO> getCourseByListId(List<Long> id);

    PageResponse<?> getAllCourseForUser(int page, int pageSize);
}
