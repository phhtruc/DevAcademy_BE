package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonRequestDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonSearchDTO;
import com.devacademy.DevAcademy_BE.service.LessonService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/lessons")
public class LessonController {

    LessonService lessonService;

    @GetMapping("/assignments")
    public ResponseEntity<?> getAllAssignment() {
        return JsonResponse.ok(lessonService.getAllAssignment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable Long id) {
        return JsonResponse.ok(lessonService.getLessonById(id));
    }

    @PostMapping
    public ResponseEntity<?> addLesson(@ModelAttribute @Valid LessonRequestDTO request,
                                       @RequestParam(required = false) MultipartFile files) {
        return JsonResponse.ok(lessonService.addLesson(request, files));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id,
                                          @ModelAttribute @Valid LessonRequestDTO request,
                                          @RequestParam(required = false) MultipartFile files) {
        return JsonResponse.ok(lessonService.updateLesson(id, request, files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return JsonResponse.deleted();
    }

    @PatchMapping("/update-order")
    public ResponseEntity<?> updateLessonOrder(@RequestBody List<OrderDTO> lessonOrderList) {
        lessonService.updateOrder(lessonOrderList);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/video-status")
    public ResponseEntity<VideoStatusResponse> getVideoUploadStatus(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getVideoUploadStatus(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLessons(@Valid LessonSearchDTO searchDTO,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(lessonService.searchLessons(searchDTO, page, pageSize));
    }

}
