package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptResponseDTO;
import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import com.devacademy.DevAcademy_BE.mapper.PromptMapper;
import com.devacademy.DevAcademy_BE.repository.PromptRepository;
import com.devacademy.DevAcademy_BE.service.PromptService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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
    public PageResponse<?> getAllReviewConfig(int page, int pageSize) {
        return null;
    }

    @Override
    public PromptResponseDTO getById(Long id) {
        return null;
    }

    @Override
    public PromptResponseDTO updateConfigActive(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
