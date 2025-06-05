package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseHasTechStackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseHasTechStackRepository extends JpaRepository<CourseHasTechStackEntity, Long> {

    Optional<List<CourseHasTechStackEntity>> findByCourseEntityId(Long courseId);

}
