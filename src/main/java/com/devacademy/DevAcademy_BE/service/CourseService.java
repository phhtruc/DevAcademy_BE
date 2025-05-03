package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseResponseDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseSearcchDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface CourseService {
    PageResponse<?> getAllCourse(int page, int size);

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO addCourse(CourseRequestDTO request, MultipartFile file) throws IOException;

    CourseResponseDTO updateCourse(Long id, CourseRequestDTO request, MultipartFile file) throws IOException;

    void deleteCourse(Long id);

    PageResponse<?> getCoursesByIdUser(int page, int pageSize, UUID id);

    PageResponse<?> getAllCourseForUser(int page, int pageSize);

    PageResponse<?> searchCourse(CourseSearcchDTO searchDTO, int page, int pageSize, String sortPrice);
}
