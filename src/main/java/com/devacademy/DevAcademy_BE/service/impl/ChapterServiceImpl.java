package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterRequestDTO;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterResponseDTO;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterSearchDTO;
import com.devacademy.DevAcademy_BE.entity.ChapterEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.ChapterMapper;
import com.devacademy.DevAcademy_BE.repository.ChapterRepository;
import com.devacademy.DevAcademy_BE.repository.CourseRepository;
import com.devacademy.DevAcademy_BE.service.ChapterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterServiceImpl implements ChapterService {

    ChapterRepository chapterRepository;
    ChapterMapper chapterMapper;
    CourseRepository courseRepository;

    @Override
    public ChapterResponseDTO getChapterById(Long id) {
        ChapterEntity chapterEntity = chapterRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_EXISTED));
        return chapterMapper.toChapterResponseDTO(chapterEntity);
    }

    @Override
    public ChapterResponseDTO addChapter(ChapterRequestDTO request) {
        courseRepository.findById(Long.parseLong(request.getCourseId()))
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_EXISTED));
        var chapterEntity = chapterMapper.toChapterEntity(request);
        var chapter_order = chapterRepository.findMaxOrderByCourseId(Long.parseLong(request.getCourseId()));
        chapterEntity.setChapterOrder(chapter_order != null? chapter_order + 1 : 1);
        chapterEntity.setIsDeleted(false);
        return chapterMapper.toChapterResponseDTO(chapterRepository.save(chapterEntity));
    }

    @Override
    public ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO request) {
        chapterRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_EXISTED));
        courseRepository.findById(Long.parseLong(request.getCourseId()))
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));
        var chapterEntity = chapterMapper.toChapterEntity(request);
        chapterEntity.setId(id);
        chapterEntity.setIsDeleted(false);
        return chapterMapper.toChapterResponseDTO(chapterRepository.save(chapterEntity));
    }

    @Override
    public void deleteChapter(Long id) {
        ChapterEntity chapterEntity = chapterRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_EXISTED));
        chapterEntity.setIsDeleted(true);
        chapterRepository.save(chapterEntity);
    }

    @Override
    public PageResponse<?> getChapterByIdCourse(int page, int pageSize, Long id) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize,
                Sort.by("chapterOrder"));
        Page<ChapterEntity> chapter = chapterRepository.findAllByCourseEntityId(id, pageable);
        List<ChapterResponseDTO> list = chapter.map(chapterMapper::toChapterResponseDTO)
                .stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(chapter.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse<?> searchChapters(ChapterSearchDTO searchDTO, int page, int pageSize) {
        return null;
    }

    @Override
    public void updateOrder(List<OrderDTO> orderDTOS) {
        List<ChapterEntity> lessonsToUpdate = orderDTOS.stream()
                .map(dto -> chapterRepository.findById(Long.parseLong(dto.getId()))
                        .map(lesson -> {
                            lesson.setChapterOrder(Integer.parseInt(dto.getOrder()));
                            return lesson;
                        })
                        .orElseThrow(() -> new ApiException(ErrorCode.LESSON_NOT_EXISTED)))
                .collect(Collectors.toList());
        chapterRepository.saveAll(lessonsToUpdate);
    }
}
