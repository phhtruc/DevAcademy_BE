package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseHasCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseHasCategoryRepository extends JpaRepository<CourseHasCategoryEntity, Long> {

    Optional<CourseHasCategoryEntity> findByCourseEntityId(Long id);
}
