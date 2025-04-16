package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.service.PromptService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/prompts")
public class PromptController {

    PromptService promptService;

    @GetMapping
    public ResponseEntity<?> getAllConfig(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(promptService.getAllReviewConfig(page, pageSize));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getByActive() {
        return JsonResponse.ok(promptService.getByActive());
    }

    @PostMapping
    public ResponseEntity<?> addConfig(@RequestBody @Valid PromptRequestDTO config) {
        return JsonResponse.ok(promptService.saveConfig(config));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConfigById(@PathVariable Long id) {
        return JsonResponse.ok(promptService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id,
                                          @RequestBody @Valid PromptRequestDTO config) {
        return JsonResponse.ok(promptService.updateConfig(id, config));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateConfigActive(@PathVariable Long id) {
        return JsonResponse.ok(promptService.updateConfigActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        promptService.delete(id);
        return JsonResponse.deleted();
    }
}
