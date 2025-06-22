package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.studentDTO.LessonTypeStatistic;
import com.devacademy.DevAcademy_BE.dto.studentDTO.StudentCourseDetailsDTO;
import com.devacademy.DevAcademy_BE.dto.studentDTO.StudentResponseDTO;
import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.CourseRegisterEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.ProgressType;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.CourseRegisterRepository;
import com.devacademy.DevAcademy_BE.repository.CourseRepository;
import com.devacademy.DevAcademy_BE.repository.ProgressRepository;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.TeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherServiceImpl implements TeacherService {

    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseRegisterRepository courseRegisterRepository;
    ProgressRepository progressRepository;
    CourseServiceImpl courseServiceImpl;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> getStudents(int page, int pageSize, Long courseId, String status, String search) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<UserEntity> students = userRepository.findEnrolledStudents(pageable, courseId);

        List<StudentResponseDTO> studentDTOs = students.getContent().stream()
                .map(user -> {
                    return StudentResponseDTO.builder()
                            .id(user.getId())
                            .name(user.getFullName())
                            .email(user.getEmail())
                            .avatar(user.getAvatar())
                            .lastActivityDate(progressRepository.findLastActivityDateByStudent(user.getId())
                                    .orElse(user.getModifiedDate()))
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(students.getTotalPages())
                .items(studentDTOs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStudentsStats() {
        long totalStudents = userRepository.countStudentsWithEnrollments();

        long activeStudents = userRepository.countByStatusAndHasEnrollments(UserStatus.ACTIVE);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("activeStudents", activeStudents);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseDetailsDTO> getStudentCourseDetails(UUID studentId) {
        userRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Get all courses the student has enrolled in
        List<CourseRegisterEntity> enrollments = courseRegisterRepository
                .findByUserEntityIdAndRegisterType(studentId, RegisterType.BUY);

        return enrollments.stream().map(enrollment -> {
            CourseEntity course = enrollment.getCourseEntity();

            List<LessonTypeStatistic> lessonStats = progressRepository.getLessonTypeStatistics(course.getId());
            Map<String, Long> lessonStatMap = lessonStats.stream()
                    .collect(Collectors.toMap(LessonTypeStatistic::getType, LessonTypeStatistic::getCount));

            List<LessonTypeStatistic> completedLessonStats = progressRepository
                    .getCompletedLessonTypeStatistics(studentId, course.getId());
            Map<String, Long> completedLessonStatsMap = completedLessonStats.stream()
                    .collect(Collectors.toMap(LessonTypeStatistic::getType, LessonTypeStatistic::getCount));

            long totalLessons = courseRepository.CountLessonsByCourse(course.getId());
            long completedLessons = progressRepository.countDistinctLessonsByUserAndCourseAndStatus(
                    studentId, course.getId(), ProgressType.COMPLETED);

            int progress = courseServiceImpl.getProgressPercent(studentId, course.getId());

            LocalDateTime lastAccessDate = progressRepository
                    .findLastAccessDateForCourseByStudent(course.getId(), studentId)
                    .orElse(null);

            return StudentCourseDetailsDTO.builder()
                    .id(course.getId())
                    .name(course.getName())
                    .thumbnailUrl(course.getThumbnailUrl())
                    .progress(progress)
                    .completedLessons(completedLessons)
                    .totalLessons(totalLessons)
                    .completedLectures(completedLessonStatsMap.getOrDefault("LECTURES", 0L))
                    .totalLectures(lessonStatMap.getOrDefault("LECTURES", 0L))
                    .completedReadings(completedLessonStatsMap.getOrDefault("READINGS", 0L))
                    .totalReadings(lessonStatMap.getOrDefault("READINGS", 0L))
                    .completedExercises(completedLessonStatsMap.getOrDefault("EXERCISES", 0L))
                    .totalExercises(lessonStatMap.getOrDefault("EXERCISES", 0L))
                    .lastAccessedDate(lastAccessDate)
                    .build();
        }).collect(Collectors.toList());
    }
}