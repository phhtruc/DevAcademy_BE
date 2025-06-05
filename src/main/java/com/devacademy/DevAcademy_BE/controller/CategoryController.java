package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryRequestDTO;
import com.devacademy.DevAcademy_BE.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {
    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(categoryService.getAll(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return JsonResponse.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CategoryRequestDTO request) {
        return JsonResponse.ok(categoryService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid CategoryRequestDTO request) {
        return JsonResponse.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return JsonResponse.deleted();
    }

}
