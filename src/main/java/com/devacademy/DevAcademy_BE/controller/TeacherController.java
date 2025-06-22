package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.userDTO.UserSearchDTO;
import com.devacademy.DevAcademy_BE.service.TeacherService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/teacher")
@PreAuthorize("hasAuthority('TEACHER')")
public class TeacherController {

    TeacherService teacherService;

    @GetMapping("/students")
    public ResponseEntity<?> getStudents(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        return JsonResponse.ok(teacherService.getStudents(page, pageSize, courseId, status, search));
    }

    @GetMapping("/students/stats")
    public ResponseEntity<?> getStudentsStats() {
        return JsonResponse.ok(teacherService.getStudentsStats());
    }

    @GetMapping("/students/{studentId}/courses")
    public ResponseEntity<?> getStudentCourseDetails(@PathVariable UUID studentId) {
        return JsonResponse.ok(teacherService.getStudentCourseDetails(studentId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStudent(@RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) Long courseId,
                                           @RequestParam(required = false) String name) {
        return JsonResponse.ok(teacherService.searchStudent(page, pageSize, courseId, name));
    }
}