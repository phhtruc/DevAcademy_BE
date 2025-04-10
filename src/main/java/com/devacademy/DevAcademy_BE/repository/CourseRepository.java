package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    @Query("SELECT c FROM CourseEntity c WHERE c.isPublic = true ORDER BY c.id ASC")
    Page<CourseEntity> findAllActiveCourses(Pageable pageable);
}
