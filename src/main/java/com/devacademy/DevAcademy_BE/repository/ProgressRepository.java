package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.ProgressEntity;
import com.devacademy.DevAcademy_BE.enums.ProgressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressEntity, Long> {
    @Query("SELECT COUNT(p) FROM ProgressEntity p " +
            "WHERE p.user.id = :userId " +
            "AND p.lessonEntity.chapterEntity.courseEntity.id = :courseId " +
            "AND p.status = :status")
    int countByUserAndLessonCourseAndStatus(UUID userId, Long courseId, ProgressType status);

    boolean existsByUserIdAndLessonEntityIdAndStatus(UUID userId, Long lessonId, ProgressType status);
}
