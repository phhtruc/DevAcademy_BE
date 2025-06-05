package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CourseRegisterEntity;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRegisterRepository extends JpaRepository<CourseRegisterEntity, Long> {

    Optional<CourseRegisterEntity> findByUserEntityIdAndCourseEntityIdAndRegisterType(
            UUID userEntityId, Long courseEntityId, RegisterType registerType);

    Integer countByCourseEntityIdAndRegisterType(Long courseId, RegisterType registerType);
}
