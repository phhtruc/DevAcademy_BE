package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterRequestDTO;
import com.devacademy.DevAcademy_BE.service.ChapterService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/chapters")
public class ChapterController {

    ChapterService chapterService;
    //LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        return JsonResponse.ok(chapterService.getChapterById(id));
    }
//
//    @GetMapping("/{id}/assignment")
//    public ResponseEntity<?> getAssignmentByIdChapter(@PathVariable Long id) {
//        return JsonResponse.ok(lessonService.getAssignmentByIdChapter(id));
//    }
//
//    @GetMapping("/{idChapter}/lessons")
//    public ResponseEntity<?> getLessonsByIdChapter(@RequestParam(required = false, defaultValue = "1") int page,
//                                                   @RequestParam(required = false, defaultValue = "10") int pageSize,
//                                                   @PathVariable Long idChapter) {
//        return JsonResponse.ok(lessonService.getLessonsByIdChapter(page, pageSize, idChapter));
//    }

    @PostMapping
    public ResponseEntity<?> addChapter(@RequestBody @Valid ChapterRequestDTO request) {
        return JsonResponse.ok(chapterService.addChapter(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @RequestBody ChapterRequestDTO request) {
        return JsonResponse.ok(chapterService.updateChapter(id, request));
    }

    @PutMapping
    public ResponseEntity<?> updateListChapter(@RequestBody ChapterRequestDTO request) {
        chapterService.updateListChapter(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return JsonResponse.deleted();
    }

//    @PatchMapping("/update-order")
//    public ResponseEntity<?> updateLessonOrder(@RequestBody List<OrderDTO> orderDTOS) {
//        chapterService.updateOrder(orderDTOS);
//        return ResponseEntity.ok().build();
//    }
}
