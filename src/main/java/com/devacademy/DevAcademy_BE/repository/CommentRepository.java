package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByLessonEntityIdAndIdOriginalCommentIsNullOrderByCreatedDateDesc(
            Long lessonId);

    List<CommentEntity> findByIdOriginalCommentInOrderByCreatedByAsc(List<Integer> parentIds);
}