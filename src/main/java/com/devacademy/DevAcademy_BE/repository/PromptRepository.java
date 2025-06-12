package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptRepository extends JpaRepository<PromptEntity, Long> {

    Page<PromptEntity> findAllPromptByCourseEntityId(Long id, Pageable pageable);

    Optional<PromptEntity> findByIdAndCourseEntityId(Long id, Long idCourse);

    Optional<PromptEntity> findPromptEntityByCourseEntityIdAndIsActive(Long courseEntityId, Boolean isActive);

    List<PromptEntity> findAllPromptByCourseEntityIdAndIsActive(Long courseEntityId, Boolean isActive);
}
