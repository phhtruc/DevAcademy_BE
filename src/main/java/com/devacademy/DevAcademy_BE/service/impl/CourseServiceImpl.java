package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseResponseDTO;
import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.CourseHasTechStackEntity;
import com.devacademy.DevAcademy_BE.entity.TechStackEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.CourseMapper;
import com.devacademy.DevAcademy_BE.repository.CourseHasTechStackRepository;
import com.devacademy.DevAcademy_BE.repository.CourseRegisterRepository;
import com.devacademy.DevAcademy_BE.repository.CourseRepository;
import com.devacademy.DevAcademy_BE.repository.TechStackRepository;
import com.devacademy.DevAcademy_BE.service.CloudinaryService;
import com.devacademy.DevAcademy_BE.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;
    CourseMapper courseMapper;
    TechStackRepository techStackRepository;
    CourseHasTechStackRepository courseHasTechStackRepository;
    CloudinaryService cloudinaryService;
    CourseRegisterRepository courseRegisterRepository;

    @Override
    public PageResponse<?> getAllCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        Page<CourseEntity> course = courseRepository.findAll(pageable);

        return getPageResponse(page, size, course);
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        var course = courseRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCode.COURSE_NOT_EXISTED));
        return courseMapper.toCourseResponseDTO(course, getTechStacksByCourse(course));
    }

    @Transactional
    @Override
    public CourseResponseDTO addCourse(CourseRequestDTO request, MultipartFile file) throws IOException {
        var course = courseMapper.toCourseEntity(request);
        course.setIsDeleted(false);
        course.setThumbnailUrl(cloudinaryService.uploadImage(file));
        courseRepository.save(course);

        var techStacks = getTechStackEntities(request);
        var courseHasTech = techStacks.stream()
                .map(tech -> {
                    var courseHasTechStack = CourseHasTechStackEntity.builder()
                            .courseEntity(course)
                            .techStackEntity(tech)
                            .build();
                    return courseHasTechStackRepository.save(courseHasTechStack);
                })
                .collect(Collectors.toSet());

        return courseMapper.toCourseResponseDTO(course, techStacks);

    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request, MultipartFile file) {
        return null;
    }

//    @Override
//    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request, MultipartFile file) {
//        var existingCourse = courseRepository.findById(id)
//                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));
//        existingCourse.getTechStackEntities()
//                .forEach(tech -> tech.getCourses().remove(existingCourse));
//        existingCourse.getTechStackEntities().clear();
//        courseRepository.flush();
//        var courseMap = courseMapper.toCourseEntity(request);
//        courseMap.setId(id);
//        courseMap.setTechStackEntities(getTechStackEntities(request, courseMap));
//        courseMap.setIsDeleted(false);
//        courseMap.setTeachers(getTeacherEntities(request));
//        if(file != null){
//            courseMap.setThumbnailUrl(imageService.upload(file));
//        }else {
//            courseMap.setThumbnailUrl(existingCourse.getThumbnailUrl());
//        }
//        return courseMapper.toCourseResponseDTO(courseRepository.save(courseMap));
//    }

    @Override
    public void deleteCourse(Long id) {
        var course = courseRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCode.COURSE_NOT_EXISTED));
        course.setIsDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public PageResponse<?> getCoursesByIdUser(int page, int pageSize, UUID id) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<CourseEntity> course = courseRepository.findAllByUserId(pageable, id);

        List<CourseResponseDTO> list = course.stream()
                .map(courseEntity -> {
                    var listTechStack = getTechStacksByCourse(courseEntity);
                    var courseMap = courseMapper.toCourseResponseDTO(courseEntity, listTechStack);
                    courseMap.setRegisterType(getRegisterTypes(id, courseEntity));
                    return courseMap;
                })
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(course.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse<?> getAllCourseForUser(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<CourseEntity> course = courseRepository.findAllPublicCourses(pageable);

        return getPageResponse(page, pageSize, course);
    }

    private PageResponse<?> getPageResponse(int page, int pageSize, Page<CourseEntity> course) {
        List<CourseResponseDTO> list = course.stream()
                .map(courseEntity -> {
                    var listTechStack = getTechStacksByCourse(courseEntity);
                    return courseMapper.toCourseResponseDTO(courseEntity, listTechStack);
                })
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(course.getTotalPages())
                .items(list)
                .build();
    }

    private List<TechStackEntity> getTechStackEntities(CourseRequestDTO requestDTO) {
        return requestDTO.getTechStack().stream()
                .map(id -> techStackRepository.findById(Long.parseLong(id))
                        .orElseThrow(() -> new ApiException(ErrorCode.TECH_STACK_NOT_EXISTED)))
                .collect(Collectors.toList());
    }

    private List<TechStackEntity> getTechStacksByCourse(CourseEntity course) {
        return courseHasTechStackRepository.findByCourseEntityId(course.getId()).stream()
                .map(CourseHasTechStackEntity::getTechStackEntity)
                .toList();
    }

    private RegisterType getRegisterTypes(UUID id, CourseEntity course) {
        var courseRegister = courseRegisterRepository
                .findCourseRegisterEntitiesByCourseEntityIdAndUserEntityId(course.getId(), id)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));
        return courseRegister.getRegisterType();
    }

//
//    private List<TeacherEntity> getTeacherEntities(CourseRequestDTO requestDTO) {
//        return requestDTO.getTeacher().stream()
//                .map(id -> teacherRepository.findById(id).orElseThrow(() ->
//                        new ApiException(ErrorCode.TEACHER_NOT_EXISTED)))
//                .collect(Collectors.toList());
//    }
}
