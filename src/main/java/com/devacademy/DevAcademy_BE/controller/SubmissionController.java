package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmitRequestDTO;
import com.devacademy.DevAcademy_BE.service.SubmissionService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<?> submission(@ModelAttribute @Valid SubmitRequestDTO request,
                                        @RequestParam(required = false) MultipartFile file) {
        return JsonResponse.ok(submissionService.submission(request, file));
    }

}
