package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    List<SubmissionEntity> findAllByUserIdAndLessonEntityIdOrderByCreatedDateDesc(UUID userId, Long lessonId);
}
