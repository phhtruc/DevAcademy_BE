package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long>, JpaSpecificationExecutor<CourseEntity> {
    @Query("SELECT c FROM CourseEntity c WHERE c.isPublic = true ORDER BY c.id ASC")
    Page<CourseEntity> findAllPublicCourses(Pageable pageable);

    @Query("""
            SELECT c FROM CourseEntity c
            JOIN CourseRegisterEntity cr ON c.id = cr.courseEntity.id
            WHERE cr.userEntity.id = :userId AND cr.status = 'CONFIRMED'
            """)
    Page<CourseEntity> findAllByUserId(Pageable pageable, UUID userId);

    @Query("""
            SELECT count(l.id) as lessonCount
            FROM CourseEntity c
            JOIN ChapterEntity ct ON c.id = ct.courseEntity.id
            JOIN LessonEntity l ON ct.id = l.chapterEntity.id
            WHERE l.type = 'LECTURES' OR l.type = 'READINGS' AND c.id = :courseId
            """)
    Integer CountLessonsByCourse(Long courseId);
}
