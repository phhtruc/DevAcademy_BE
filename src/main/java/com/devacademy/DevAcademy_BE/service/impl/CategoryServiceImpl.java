package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryRequestDTO;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryResponseDTO;
import com.devacademy.DevAcademy_BE.entity.CategoryEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.CategoryMapper;
import com.devacademy.DevAcademy_BE.repository.CategoryRepository;
import com.devacademy.DevAcademy_BE.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public PageResponse<?> getAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryResponseDTO> cateResponse = categoryPage.map(categoryMapper::toCategoryResponseDTO)
                .stream().collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(categoryPage.getTotalPages())
                .items(cateResponse)
                .build();
    }

    @Override
    public CategoryResponseDTO getById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUNT));
        return categoryMapper.toCategoryResponseDTO(categoryEntity);
    }

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(request);
        categoryEntity.setIsDeleted(false);
        categoryEntity = categoryRepository.save(categoryEntity);
        return categoryMapper.toCategoryResponseDTO(categoryEntity);
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUNT));
        var category = categoryMapper.toCategoryEntity(request);
        category.setId(id);
        category.setIsDeleted(false);

        return categoryMapper.toCategoryResponseDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUNT));
        categoryEntity.setIsDeleted(true);
        categoryRepository.save(categoryEntity);
    }
}
