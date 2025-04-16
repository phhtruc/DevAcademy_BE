package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackRequestDTO;
import com.devacademy.DevAcademy_BE.service.TechStackService;
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
@RequestMapping("/api/v1/tech-stacks")
public class TechStackController {
    TechStackService techStackService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(techStackService.getAll(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return JsonResponse.ok(techStackService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid TechStackRequestDTO request) {
        return JsonResponse.ok(techStackService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid TechStackRequestDTO request) {
        return JsonResponse.ok(techStackService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        techStackService.delete(id);
        return JsonResponse.deleted();
    }

}
