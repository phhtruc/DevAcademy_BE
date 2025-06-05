package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackRequestDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import com.devacademy.DevAcademy_BE.entity.TechStackEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.TechStackMapper;
import com.devacademy.DevAcademy_BE.repository.TechStackRepository;
import com.devacademy.DevAcademy_BE.service.TechStackService;
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
public class TechStackServiceImpl implements TechStackService {

    TechStackRepository techStackRepository;
    TechStackMapper techStackMapper;

    @Override
    public PageResponse<?> getAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<TechStackEntity> techStackEntities = techStackRepository.findAll(pageable);
        List<TechStackResponseDTO> techStackResponseDTOS = techStackEntities.map(techStackMapper::toTechStackResponse)
                .stream().collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(techStackEntities.getTotalPages())
                .items(techStackResponseDTOS)
                .build();
    }

    @Override
    public TechStackResponseDTO getById(Long id) {
        TechStackEntity techStack = techStackRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.TECH_STACK_NOT_EXISTED));
        return techStackMapper.toTechStackResponse(techStack);
    }

    @Override
    public TechStackResponseDTO create(TechStackRequestDTO request) {
        TechStackEntity techStack = techStackMapper.toTechStackEntity(request);
        techStack.setIsDeleted(false);
        return techStackMapper.toTechStackResponse(techStackRepository.save(techStack));
    }

    @Override
    public TechStackResponseDTO update(Long id, TechStackRequestDTO request) {
        techStackRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.TECH_STACK_NOT_EXISTED));
        var category = techStackMapper.toTechStackEntity(request);
        category.setId(id);
        category.setIsDeleted(false);

        return techStackMapper.toTechStackResponse(techStackRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        TechStackEntity techStack = techStackRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.TECH_STACK_NOT_EXISTED));
        techStack.setIsDeleted(true);
        techStackRepository.save(techStack);
    }
}
