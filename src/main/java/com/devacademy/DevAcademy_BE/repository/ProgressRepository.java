package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.dto.studentDTO.LessonTypeStatistic;
import com.devacademy.DevAcademy_BE.entity.ProgressEntity;
import com.devacademy.DevAcademy_BE.enums.ProgressType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressEntity, Long> {
    @Query("""
                SELECT COUNT(DISTINCT p.lessonEntity.id)
                FROM ProgressEntity p
                WHERE p.user.id = :userId
                  AND p.lessonEntity.chapterEntity.courseEntity.id = :courseId
                  AND p.status = :status
            """)
    int countDistinctLessonsByUserAndCourseAndStatus(UUID userId, Long courseId, ProgressType status);

    boolean existsByUserIdAndLessonEntityIdAndStatus(UUID userId, Long lessonId, ProgressType status);

    // status
    @Query("SELECT MAX(p.modifiedDate) FROM ProgressEntity p WHERE p.user.id = :studentId")
    Optional<LocalDateTime> findLastActivityDateByStudent(@Param("studentId") UUID studentId);

    // details
    @Query("""
                SELECT l.type as type, COUNT(l.id) as count
                FROM LessonEntity l
                WHERE l.chapterEntity.courseEntity.id = :courseId
                GROUP BY l.type
            """)
    List<LessonTypeStatistic> getLessonTypeStatistics(@Param("courseId") Long courseId);

    @Query("""
            SELECT l.type as type, COUNT(DISTINCT l.id) as count
            FROM ProgressEntity p
            JOIN p.lessonEntity l
            WHERE p.user.id = :studentId
            AND l.chapterEntity.courseEntity.id = :courseId
            AND p.status = 'COMPLETED'
            GROUP BY l.type
            """)
    List<LessonTypeStatistic> getCompletedLessonTypeStatistics(UUID studentId, Long courseId);

    @Query("""
            SELECT MAX(p.modifiedDate)
            FROM ProgressEntity p
            WHERE p.user.id = :studentId
            AND p.lessonEntity.chapterEntity.courseEntity.id = :courseId
            """)
    Optional<LocalDateTime> findLastAccessDateForCourseByStudent(Long courseId, UUID studentId);
}
