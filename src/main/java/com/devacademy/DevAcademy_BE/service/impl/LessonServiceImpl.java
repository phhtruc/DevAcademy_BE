package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import com.devacademy.DevAcademy_BE.dto.VideoUploadTask;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonRequestDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonResponseDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonSearchDTO;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.TypeLesson;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.LessonMapper;
import com.devacademy.DevAcademy_BE.repository.ChapterRepository;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import com.devacademy.DevAcademy_BE.repository.specification.LessonSpecification;
import com.devacademy.DevAcademy_BE.service.LessonService;
import com.devacademy.DevAcademy_BE.service.VideoUploadQueueService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class LessonServiceImpl implements LessonService {

    LessonRepository lessonRepository;
    LessonMapper lessonMapper;
    ChapterRepository chapterRepository;
    VideoUploadQueueService videoUploadQueueService;
    RedisTemplate<String, VideoStatusResponse> redisTemplate;
    static String REDIS_VIDEO_STATUS_KEY = "video:status:";

    @Override
    public PageResponse<?> getLessonsByIdChapter(int page, int pageSize, Long idChapter) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize,
                Sort.by("lessonOrder"));
        Page<LessonEntity> course = lessonRepository.findAllByChapterEntityId(idChapter, pageable);
        List<LessonResponseDTO> list = course.map(lessonMapper::toLessonResponseDTO)
                .stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(course.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public LessonResponseDTO getLessonById(Long id) {
        var lesson = lessonRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCode.LESSON_NOT_EXISTED));
        return lessonMapper.toLessonResponseDTO(lesson);
    }

    @Override
    public LessonResponseDTO addLesson(LessonRequestDTO request, MultipartFile video) {
        chapterRepository.findById(Long.parseLong(request.getChapterId()))
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_FOUND));
        var lesson = lessonMapper.toLessonEntity(request);
        var lesson_order = lessonRepository.findMaxOrderByChapterId(Long.parseLong(request.getChapterId()));
        lesson.setLessonOrder(lesson_order != null ? lesson_order + 1 : 1);
        lesson.setIsDeleted(false);

        var savedLesson = lessonRepository.save(lesson);

        // Đưa video vào hàng đợi upload
        if (video != null && !video.isEmpty()) {
            videoUploadQueueService.addToQueue(new VideoUploadTask(video, savedLesson.getId()));
        }

        return lessonMapper.toLessonResponseDTO(savedLesson);
    }

    @Override
    public LessonResponseDTO updateLesson(Long id, LessonRequestDTO request, MultipartFile video) {
        var existingLesson = lessonRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCode.LESSON_NOT_EXISTED));
        chapterRepository.findById(Long.parseLong(request.getChapterId()))
                .orElseThrow(() -> new ApiException(ErrorCode.CHAPTER_NOT_FOUND));

        var lesson = lessonMapper.toLessonEntity(request);
        lesson.setId(id);
        lesson.setIsDeleted(false);
        lesson.setLessonOrder(existingLesson.getLessonOrder());
        var savedLesson = lessonRepository.save(lesson);

        if (video != null && !video.isEmpty()) {
            videoUploadQueueService.addToQueue(new VideoUploadTask(video, savedLesson.getId()));
        }
        return lessonMapper.toLessonResponseDTO(savedLesson);
    }

    @Override
    public void deleteLesson(Long id) {
        var user = lessonRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.setIsDeleted(true);
        lessonRepository.save(user);
    }

    @Override
    public void updateOrder(List<OrderDTO> lessonOrderList) {
        List<LessonEntity> lessonsToUpdate = lessonOrderList.stream()
                .map(dto -> lessonRepository.findById(Long.parseLong(dto.getId()))
                        .map(lesson -> {
                            lesson.setLessonOrder(Integer.parseInt(dto.getOrder()));
                            return lesson;
                        })
                        .orElseThrow(() -> new ApiException(ErrorCode.LESSON_NOT_EXISTED)))
                .collect(Collectors.toList());
        lessonRepository.saveAll(lessonsToUpdate);
    }

    @Override
    public List<LessonResponseDTO> getAssignmentByIdChapter(Long id) {
        return lessonRepository.findByChapterId(id)
                .stream().map(lessonMapper::toLessonResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LessonResponseDTO> getAllAssignment() {
        return lessonRepository.findAllByType(TypeLesson.EXERCISES)
                .stream().map(lessonMapper::toLessonResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VideoStatusResponse getVideoUploadStatus(Long lessonId) {
        return redisTemplate.opsForValue().get(REDIS_VIDEO_STATUS_KEY + lessonId);
    }

    @Override
    public PageResponse<?> searchLessons(LessonSearchDTO searchDTO, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize,
                Sort.by("lessonOrder"));

        Specification<LessonEntity> spec = Specification
                .where(LessonSpecification.hasTitle(searchDTO))
                ;
        Page<LessonEntity> course = lessonRepository.findAll(spec, pageable);
        List<LessonResponseDTO> list = course.map(lessonMapper::toLessonResponseDTO)
                .stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(course.getTotalPages())
                .items(list)
                .build();
    }

}
