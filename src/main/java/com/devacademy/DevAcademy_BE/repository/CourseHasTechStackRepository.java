package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseHasTechStackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseHasTechStackRepository extends JpaRepository<CourseHasTechStackEntity, Long> {
}
