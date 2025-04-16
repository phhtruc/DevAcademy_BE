package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptResponseDTO;
import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.PromptMapper;
import com.devacademy.DevAcademy_BE.repository.PromptRepository;
import com.devacademy.DevAcademy_BE.service.PromptService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@RequiredArgsConstructor
@Transactional
public class PromptServiceImpl implements PromptService {

    PromptRepository promptRepository;
    PromptMapper promptMapper;

    @Override
    public PromptResponseDTO getByActive() {
        return null;
    }

    @Override
    public PromptResponseDTO saveConfig(PromptRequestDTO config) {
        PromptEntity promptEntity = promptMapper.toPromptEntity(config);
        promptEntity.setIsDeleted(false);
        promptEntity.setIsActive(false);
        return promptMapper.toPromptConfigResponseDTO(promptRepository.save(promptEntity));
    }

    @Override
    public PromptResponseDTO updateConfig(Long id, PromptRequestDTO config) {
        return null;
    }

    @Override
    public PageResponse<?> getAllReviewConfig(int page, int pageSize, Long courseId) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<PromptEntity> prompt;

        if (courseId != null) {
            prompt = promptRepository.findAllPromptByCourseEntityId(courseId, pageable);
        } else {
            prompt = promptRepository.findAll(pageable);
        }

        List<PromptResponseDTO> list = prompt.map(promptMapper::toPromptConfigResponseDTO)
                .stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(prompt.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PromptResponseDTO getById(Long id) {
        PromptEntity promptEntity = promptRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PROMPT_NOT_FOUNT));
        return promptMapper.toPromptConfigResponseDTO(promptEntity);
    }

    @Override
    public PromptResponseDTO updateConfigActive(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
        PromptEntity promptEntity = promptRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PROMPT_NOT_FOUNT));
        promptEntity.setIsDeleted(true);
    }

}
