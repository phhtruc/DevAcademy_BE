package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryRequestDTO;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryResponseDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackRequestDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface TechStackService {

    PageResponse<?> getAll(int page, int pageSize);

    TechStackResponseDTO getById(Long id);

    TechStackResponseDTO create(TechStackRequestDTO request);

    TechStackResponseDTO update(Long id, TechStackRequestDTO request);

    void delete(Long id);
}
