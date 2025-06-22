package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.studentDTO.StudentCourseDetailsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TeacherService {
    PageResponse<?> getStudents(int page, int pageSize, Long courseId, String status, String search);

    Object getStudentsStats();

    List<StudentCourseDetailsDTO> getStudentCourseDetails(UUID studentId);
}
