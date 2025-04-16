package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryRequestDTO;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    PageResponse<?> getAll(int page, int pageSize);

    CategoryResponseDTO getById(Long id);

    CategoryResponseDTO create(CategoryRequestDTO request);

    CategoryResponseDTO update(Long id, CategoryRequestDTO request);

    void delete(Long id);
}
