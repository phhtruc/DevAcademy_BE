package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryResponseDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseResponseDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseSearchDTO;
import com.devacademy.DevAcademy_BE.entity.*;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.CategoryMapper;
import com.devacademy.DevAcademy_BE.mapper.CourseMapper;
import com.devacademy.DevAcademy_BE.repository.*;
import com.devacademy.DevAcademy_BE.repository.specification.CourseSpecification;
import com.devacademy.DevAcademy_BE.service.CourseService;
import com.devacademy.DevAcademy_BE.service.MailService;
import com.devacademy.DevAcademy_BE.service.cloudinary.CloudinaryService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
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
    CategoryRepository categoryRepository;
    CourseHasCategoryRepository courseHasCategoryRepository;
    CategoryMapper categoryMapper;
    MailService mailService;

    @Override
    public PageResponse<?> getAllCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        Page<CourseEntity> course = courseRepository.findAll(pageable);

        return getPageResponse(page, size, course, null);
    }

    @Override
    public CourseResponseDTO getCourseById(Long id, Authentication auth) throws MessagingException {
        var course = courseRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCode.COURSE_NOT_EXISTED));
        var courseMap = courseMapper.toCourseResponseDTO(course, getTechStacksByCourse(course));
        courseMap.setCategory(getCategory(course));
        courseMap.setLessonCount(getLessonCountByCourse(course.getId()));
        courseMap.setTotalRegister(countUserBuyCourse(course.getId()));

        if(auth != null && auth.getPrincipal() instanceof UserEntity userEntity) {
            courseMap.setIsPurchased(checkCourseIsPurchased(userEntity.getId(), id));
            if( courseMap.getIsPurchased() ) {
                courseMap.setDuration(Objects.requireNonNull(getCourseDaysRemaining(userEntity.getId(), id, userEntity)).toString());
            }
        }

        return courseMap;
    }

    @Transactional
    @Override
    public CourseResponseDTO addCourse(CourseRequestDTO request, MultipartFile file) throws IOException {
        CourseEntity course = prepareCourseEntity(request, file, null);
        courseRepository.save(course);
        saveCourseTechStacks(course, request);
        var courseMap =  courseMapper.toCourseResponseDTO(course, getTechStackEntities(request));
        courseMap.setCategory(getCategory(course));
        return courseMap;
    }

    @Transactional
    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request, MultipartFile file) throws IOException {
        courseRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));

        var techStacksHasCourse = courseHasTechStackRepository.findByCourseEntityId(id)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_TECH_STACK_NOT_FOUNT));
        courseHasTechStackRepository.deleteAll(techStacksHasCourse);

        courseHasCategoryRepository.findByCourseEntityId(id).ifPresent(courseHasCategoryRepository::delete);

        CourseEntity updatedCourse = prepareCourseEntity(request, file, id);
        courseRepository.save(updatedCourse);
        saveCourseTechStacks(updatedCourse, request);
        var courseMap =  courseMapper.toCourseResponseDTO(updatedCourse, getTechStackEntities(request));
        courseMap.setCategory(getCategory(updatedCourse));
        return courseMap;
    }

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

        return getPageResponse(page, pageSize, course, id);
    }

    @Override
    public PageResponse<?> getAllCourseForUser(int page, int pageSize, Authentication authentication) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<CourseEntity> course = courseRepository.findAllPublicCourses(pageable);

        if(authentication == null || !(authentication.getPrincipal() instanceof UserEntity userEntity)) {
            return getPageResponse(page, pageSize, course, null);
        }

        return getPageResponse(page, pageSize, course, userEntity.getId());
    }

    @Override
    public PageResponse<?> searchCourse(CourseSearchDTO searchDTO, int page, int pageSize, String sortPrice) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);

        Specification<CourseEntity> spec = Specification
                .where(CourseSpecification.hasTitle(searchDTO.getName()))
                .and(CourseSpecification.hasCourseType(searchDTO.getType()))
                .and(CourseSpecification.hasCateId(searchDTO.getCategoryId()))
                .and(CourseSpecification.sortPrice(sortPrice));

        Page<CourseEntity> courseEntityPage = courseRepository.findAll(spec, pageable);

        List<CourseResponseDTO> courseList = courseEntityPage.stream()
                .map(course -> {
                    var techStacks = getTechStacksByCourse(course);
                    var courseMap = courseMapper.toCourseResponseDTO(course, techStacks);
                    courseMap.setCategory(getCategory(course));
                    return courseMap;
                })
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(courseEntityPage.getTotalPages())
                .items(courseList)
                .build();
    }

    private CourseEntity prepareCourseEntity(CourseRequestDTO request, MultipartFile file, Long id) throws IOException {
        CourseEntity course = courseMapper.toCourseEntity(request);
        course.setId(id);
        course.setIsDeleted(false);
        if (file != null && !file.isEmpty()) {
            course.setThumbnailUrl(cloudinaryService.uploadImage(file));
        }
        if(request.getIdCategory() != null && !request.getIdCategory().isBlank()) {
            CategoryEntity categoryEntity = categoryRepository.findById(Long.parseLong(request.getIdCategory()))
                    .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUNT));
            saveCategoryHasCourse(course, categoryEntity);
        }

        return course;
    }

    private void saveCourseTechStacks(CourseEntity course, CourseRequestDTO request) {
        List<TechStackEntity> techStacks = getTechStackEntities(request);
        techStacks.forEach(tech -> {
            CourseHasTechStackEntity relation = CourseHasTechStackEntity.builder()
                    .courseEntity(course)
                    .techStackEntity(tech)
                    .build();
            courseHasTechStackRepository.save(relation);
        });
    }

    private void saveCategoryHasCourse(CourseEntity course, CategoryEntity category) {
        CourseHasCategoryEntity relation = CourseHasCategoryEntity.builder()
                .courseEntity(course)
                .categoryEntity(category)
                .build();
        courseHasCategoryRepository.save(relation);
    }

    private PageResponse<?> getPageResponse(int page, int pageSize, Page<CourseEntity> course, UUID userId) {
        List<CourseResponseDTO> list = course.stream()
                .map(courseEntity -> {
                    var listTechStack = getTechStacksByCourse(courseEntity);
                    var lessonCount = getLessonCountByCourse(courseEntity.getId());
                    var courseMap = courseMapper.toCourseResponseDTO(courseEntity, listTechStack);
                    courseMap.setCategory(getCategory(courseEntity));
                    courseMap.setLessonCount(lessonCount);
                    if (userId != null) {
                        courseMap.setIsPurchased(checkCourseIsPurchased(userId, courseEntity.getId()));
                    } else {
                        courseMap.setIsPurchased(false);
                    }
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

    private List<TechStackEntity> getTechStackEntities(CourseRequestDTO requestDTO) {
        return requestDTO.getTechStack().stream()
                .map(id -> techStackRepository.findById(Long.parseLong(id))
                        .orElseThrow(() -> new ApiException(ErrorCode.TECH_STACK_NOT_EXISTED)))
                .collect(Collectors.toList());
    }

    private List<TechStackEntity> getTechStacksByCourse(CourseEntity course) {
        return courseHasTechStackRepository.findByCourseEntityId(course.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_TECH_STACK_NOT_FOUNT))
                .stream()
                .map(CourseHasTechStackEntity::getTechStackEntity)
                .toList();
    }

    private CategoryResponseDTO getCategory(CourseEntity course) {
        var courseHasCategory = categoryRepository.findByCourseId(course.getId())
                .orElse(null);
        return categoryMapper.toCategoryResponseDTO(courseHasCategory);
    }

    private int getLessonCountByCourse(Long courseId) {
        return courseRepository.CountLessonsByCourse(courseId);
    }

    private boolean checkCourseIsPurchased(UUID userId, Long courseId) {
        return courseRegisterRepository
                .findByUserEntityIdAndCourseEntityIdAndRegisterType(userId, courseId, RegisterType.BUY)
                .isPresent();
    }

    private Integer countUserBuyCourse(Long courseId) {
        return courseRegisterRepository.countByCourseEntityIdAndRegisterType(courseId, RegisterType.BUY);
    }

    private Integer getCourseDaysRemaining(UUID userId, Long courseId, UserEntity user) throws MessagingException {
        CourseRegisterEntity registration = courseRegisterRepository
                .findByUserEntityIdAndCourseEntityIdAndRegisterType(userId, courseId, RegisterType.BUY)
                .orElse(null);
        CourseEntity courseEntity = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_EXISTED));

        if (registration == null) {
            return null;
        }
        int durationInMonths = courseEntity.getDuration();
        LocalDateTime purchaseDate = registration.getCreatedDate();
        LocalDateTime expirationDate = purchaseDate.plusMonths(durationInMonths);
        long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), expirationDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDateTime.now().format(formatter);

        if (daysRemaining <= 0) {
            registration.setIsDeleted(true);
            courseRegisterRepository.save(registration);
            mailService.durationCourse(user.getFullName(), formattedDate, courseEntity.getName(),
                    registration.getUserEntity().getEmail(), "Khoá học đã hết hạn", courseId);
            return 0;
        }

        return (int) daysRemaining;
    }
}
