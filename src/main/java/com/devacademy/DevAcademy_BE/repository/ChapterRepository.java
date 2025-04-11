package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    Page<ChapterEntity> findAllByCourseEntityId(Long id, Pageable pageable);

    @Query("SELECT MAX(c.chapterOrder) FROM ChapterEntity c WHERE c.courseEntity.id = :courseId")
    Integer findMaxOrderByCourseId(@Param("courseId") Long courseId);

    Optional<List<ChapterEntity>> findAllByCourseEntityId(Long courseId);
}
