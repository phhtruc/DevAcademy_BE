package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.enums.TypeLesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long>, JpaSpecificationExecutor<LessonEntity> {

    Page<LessonEntity> findAllByChapterEntityId(Long idChapter, Pageable pageable);

    @Query("SELECT MAX(c.lessonOrder) FROM LessonEntity c WHERE c.chapterEntity.id = :chapterId")
    Integer findMaxOrderByChapterId(@Param("chapterId") Long chapterId);

    @Query("""
        select l from LessonEntity l
        where l.chapterEntity.id = :chapterId
        and l.type = 'EXERCISES'
        """)
    List<LessonEntity> findByChapterId(@Param("chapterId") Long chapterId);

    List<LessonEntity> findAllByType(TypeLesson type);

}

