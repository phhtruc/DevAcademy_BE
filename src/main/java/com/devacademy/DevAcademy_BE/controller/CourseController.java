package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseSearchDTO;
import com.devacademy.DevAcademy_BE.service.ChapterService;
import com.devacademy.DevAcademy_BE.service.CourseService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
public class CourseController {

    CourseService courseService;
    ChapterService chapterService;

    @GetMapping
    public ResponseEntity<?> getAllCourse(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(courseService.getAllCourse(page, pageSize));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllCourseForUser(@RequestParam(required = false, defaultValue = "1") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                 Authentication authentication) {
        return JsonResponse.ok(courseService.getAllCourseForUser(page, pageSize, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, Authentication auth) throws MessagingException {
        return JsonResponse.ok(courseService.getCourseById(id, auth));
    }

    @GetMapping("/{id}/chapters")
    public ResponseEntity<?> getChapterByIdCourse(@RequestParam(required = false, defaultValue = "1") int page,
                                                  @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                  @PathVariable Long id) {
        return JsonResponse.ok(chapterService.getChapterByIdCourse(page, pageSize, id));
    }

    @PostMapping
    public ResponseEntity<?> addCourse(@RequestParam(value = "file") MultipartFile multipartFile,
                                       @ModelAttribute @Valid CourseRequestDTO request) throws IOException {
        return JsonResponse.ok(courseService.addCourse(request, multipartFile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id,
                                          @RequestParam(value = "file") MultipartFile file,
                                          @ModelAttribute @Valid CourseRequestDTO request) throws IOException {
        return JsonResponse.ok(courseService.updateCourse(id, request, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return JsonResponse.deleted();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProperties(@Valid CourseSearchDTO searchDTO,
                                              @RequestParam(required = false, defaultValue = "1") int page,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                                              @RequestParam(required = false) String sortPrice) {
        return JsonResponse.ok(courseService.searchCourse(searchDTO, page, pageSize, sortPrice));
    }
}
